package com.emmsale.event_publisher;

import com.emmsale.comment.event.UpdateNotificationEvent;
import com.emmsale.event.domain.Event;
import com.emmsale.member.domain.InterestTagRepository;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.notification.domain.UpdateNotificationType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventPublisher {

  private static final String UPDATE_NOTIFICATION_EVENT_TYPE = "event";

  private final ApplicationEventPublisher applicationEventPublisher;
  private final InterestTagRepository interestTagRepository;
  private final MemberRepository memberRepository;

  public void publish(final UpdateNotificationEvent event) {
    applicationEventPublisher.publishEvent(event);
  }

  public void publish(final Event event) {
    final List<Long> tagIds = event.getTags()
        .stream()
        .map(it -> it.getTag().getId())
        .collect(Collectors.toList());

    final Set<Long> memberIds = interestTagRepository.findInterestTagsByTagIdIn(tagIds)
        .stream()
        .collect(Collectors.groupingBy(it -> it.getMember().getId()))
        .keySet();

    final List<Member> members = memberRepository.findAllByIdIn(memberIds);

    publishEvent(event, members);
  }

  private void publishEvent(final Event event, final List<Member> members) {
    for (Member member : members) {
      final UpdateNotificationEvent updateNotificationEvent = new UpdateNotificationEvent(
          member.getId(),
          event.getId(),
          UpdateNotificationType.from(UPDATE_NOTIFICATION_EVENT_TYPE).toString(),
          LocalDateTime.now()
      );

      applicationEventPublisher.publishEvent(updateNotificationEvent);
    }
  }
}
