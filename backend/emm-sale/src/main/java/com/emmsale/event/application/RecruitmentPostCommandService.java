package com.emmsale.event.application;

import static com.emmsale.event.exception.EventExceptionType.NOT_FOUND_EVENT;
import static com.emmsale.event.exception.EventExceptionType.NOT_FOUND_RECRUITMENT_POST;

import com.emmsale.event.application.dto.RecruitmentPostRequest;
import com.emmsale.event.application.dto.RecruitmentPostUpdateRequest;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.RecruitmentPost;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.domain.repository.RecruitmentPostRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RecruitmentPostCommandService {

  private final RecruitmentPostRepository recruitmentPostRepository;
  private final EventRepository eventRepository;

  private static void validateMemberNotAllowed(final Long memberId, final Member member) {
    if (member.isNotMe(memberId)) {
      throw new EventException(EventExceptionType.FORBIDDEN_CREATE_RECRUITMENT_POST);
    }
  }

  public Long createRecruitmentPost(
      final Long eventId,
      final RecruitmentPostRequest request,
      final Member member
  ) {
    final Long memberId = request.getMemberId();
    final String content = request.getContent();
    validateMemberNotAllowed(memberId, member);
    final Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventException(NOT_FOUND_EVENT));

    final RecruitmentPost recruitmentPost = event.createRecruitmentPost(member, content);
    recruitmentPostRepository.save(recruitmentPost);
    return recruitmentPost.getId();
  }

  public void deleteRecruitmentPost(final Long eventId, final Long postId, final Member member) {
    final RecruitmentPost recruitmentPost = recruitmentPostRepository.findById(postId)
        .orElseThrow(() -> new EventException(NOT_FOUND_RECRUITMENT_POST));
    recruitmentPost.validateEvent(eventId);
    recruitmentPost.validateOwner(member);
    recruitmentPostRepository.deleteById(recruitmentPost.getId());
  }

  public void updateRecruitmentPost(
      final Long eventId,
      final Long postId,
      final RecruitmentPostUpdateRequest request,
      final Member member
  ) {
    final RecruitmentPost recruitmentPost = recruitmentPostRepository.findById(postId)
        .orElseThrow(() -> new EventException(NOT_FOUND_RECRUITMENT_POST));
    recruitmentPost.validateEvent(eventId);
    recruitmentPost.validateOwner(member);
    recruitmentPost.updateContent(request.getContent());
  }
}
