package com.emmsale.event_publisher;

import com.emmsale.comment.domain.Comment;
import com.emmsale.comment.domain.CommentRepository;
import com.emmsale.event.domain.Event;
import com.emmsale.member.domain.InterestTagRepository;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.message_room.domain.Message;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
  private final CommentRepository commentRepository;

  public void publish(final Comment trigger, final Member loginMember) {
    final Set<Comment> notificationCommentCandidates = trigger.getParent()
        .map(parent -> findRelatedCommentsExcludingLoginMember(loginMember, parent))
        .orElse(Collections.emptySet());

    notificationCommentCandidates.stream()
        .map(it -> UpdateNotificationEvent.of(it, trigger.getId()))
        .forEach(applicationEventPublisher::publishEvent);
  }

  private Set<Comment> findRelatedCommentsExcludingLoginMember(
      final Member loginMember,
      final Comment parent
  ) {
    return new HashSet<>(
        commentRepository.findParentAndChildrenByParentId(parent.getId())
            .stream()
            .filter(it -> it.isNotMyComment(loginMember))
            .filter(Comment::isNotDeleted)
            .collect(Collectors.toMap(
                comment -> comment.getMember().getId(),
                comment -> comment, (existed, replaces) -> existed)
            )
            .values()
    );
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
    members.forEach(
        it -> applicationEventPublisher.publishEvent(
            UpdateNotificationEvent.of(event, it.getId())
        )
    );
  }

  public void publish(final Message message, final Long receiverId) {
    final MessageNotificationEvent event = MessageNotificationEvent.of(message, receiverId);
    applicationEventPublisher.publishEvent(event);
  }
}
