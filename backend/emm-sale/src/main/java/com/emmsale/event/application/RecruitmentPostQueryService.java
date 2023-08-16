package com.emmsale.event.application;

import static com.emmsale.event.exception.EventExceptionType.NOT_FOUND_EVENT;
import static com.emmsale.event.exception.EventExceptionType.NOT_FOUND_RECRUITMENT_POST;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toUnmodifiableList;

import com.emmsale.event.application.dto.RecruitmentPostResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.RecruitmentPost;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.domain.repository.RecruitmentPostRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.member.domain.Member;
import com.emmsale.notification.domain.RequestNotification;
import com.emmsale.notification.domain.RequestNotificationRepository;
import com.emmsale.notification.domain.RequestNotificationStatus;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentPostQueryService {

  private final RecruitmentPostRepository recruitmentPostRepository;
  private final EventRepository eventRepository;
  private final RequestNotificationRepository requestNotificationRepository;

  public List<RecruitmentPostResponse> findRecruitmentPosts(final Long eventId,
      final Member member) {
    final Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventException(NOT_FOUND_EVENT));
    return event.getRecruitmentPosts().stream()
        .sorted(comparing(RecruitmentPost::getId))
        .map(recruitmentPost -> RecruitmentPostResponse.from(recruitmentPost,
            calculateStatus(recruitmentPost, member)))
        .collect(toUnmodifiableList());
  }

  public Boolean isAlreadyRecruit(final Long eventId, final Long memberId) {
    return recruitmentPostRepository.existsByEventIdAndMemberId(eventId, memberId);
  }

  public RecruitmentPostResponse findRecruitmentPost(final Long eventId, final Long postId,
      final Member member) {
    final RecruitmentPost recruitmentPost = recruitmentPostRepository.findById(postId)
        .orElseThrow(() -> new EventException(NOT_FOUND_RECRUITMENT_POST));
    recruitmentPost.validateEvent(eventId);
    return RecruitmentPostResponse.from(recruitmentPost, calculateStatus(recruitmentPost, member));
  }

  private RequestNotificationStatus calculateStatus(RecruitmentPost recruitmentPost,
      Member member) {
    Optional<RequestNotification> requestNotification = requestNotificationRepository.findBySenderIdAndReceiverIdAndEventId(
        member.getId(),
        recruitmentPost.getMember().getId(), recruitmentPost.getEvent().getId());
    if (requestNotification.isEmpty()) {
      return RequestNotificationStatus.NOT_REQUEST;
    }
    return requestNotification.get().getStatus();
  }
}
