package com.emmsale.event_publisher;

import com.emmsale.comment.event.UpdateNotificationEvent;
import com.emmsale.event.domain.Event;
import com.emmsale.member.domain.InterestTag;
import com.emmsale.member.domain.InterestTagRepository;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.notification.domain.UpdateNotificationType;
import com.emmsale.tag.domain.Tag;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;
  private final InterestTagRepository interestTagRepository;
  private final MemberRepository memberRepository;

  public void publish(final UpdateNotificationEvent event) {
    applicationEventPublisher.publishEvent(event);
  }

  public void publish(final Event event) {

    final List<Long> memberIds = memberRepository.findAll()
        .stream()
        .map(Member::getId)
        .collect(Collectors.toList());

    for (final Long memberId : memberIds) {
      final List<Tag> interestTags = interestTagRepository.findInterestTagsByMemberId(memberId)
          .stream()
          .map(InterestTag::getTag)
          .collect(Collectors.toList());

      if (event.hasSameTagFrom(interestTags)) {
        final UpdateNotificationEvent updateNotificationEvent = new UpdateNotificationEvent(
            memberId,
            event.getId(),
            UpdateNotificationType.from(event.getClass().getName()).toString(),
            LocalDateTime.now()
        );

        applicationEventPublisher.publishEvent(updateNotificationEvent);
      }
    }
  }
}
