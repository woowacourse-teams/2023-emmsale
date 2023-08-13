package com.emmsale.event.application;

import static com.emmsale.event.exception.EventExceptionType.NOT_FOUND_EVENT;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toUnmodifiableList;

import com.emmsale.event.application.dto.RecruitmentPostResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.RecruitmentPost;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.domain.repository.RecruitmentPostRepository;
import com.emmsale.event.exception.EventException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentPostQueryService {

  private final RecruitmentPostRepository recruitmentPostRepository;
  private final EventRepository eventRepository;

  public List<RecruitmentPostResponse> findRecruitmentPosts(final Long eventId) {
    final Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventException(NOT_FOUND_EVENT));
    return event.getRecruitmentPosts().stream()
        .sorted(comparing(RecruitmentPost::getId))
        .map(RecruitmentPostResponse::from)
        .collect(toUnmodifiableList());
  }

  public Boolean isAlreadyRecruit(final Long eventId, final Long memberId) {
    return recruitmentPostRepository.existsByEventIdAndMemberId(eventId, memberId);
  }

  public RecruitmentPostResponse findRecruitmentPost(final Long eventId, final Long postId) {
    return null;
  }
}
