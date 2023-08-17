package com.emmsale.event.application;

import static com.emmsale.event.exception.EventExceptionType.NOT_FOUND_EVENT;
import static com.emmsale.event.exception.EventExceptionType.NOT_FOUND_RECRUITMENT_POST;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toUnmodifiableList;

import com.emmsale.event.application.dto.RecruitmentPostQueryResponse;
import com.emmsale.event.application.dto.RecruitmentPostResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.RecruitmentPost;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.domain.repository.RecruitmentPostRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.member.domain.Member;
import com.emmsale.member.exception.MemberException;
import com.emmsale.member.exception.MemberExceptionType;
import java.util.List;
import java.util.stream.Collectors;
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
    final RecruitmentPost recruitmentPost = recruitmentPostRepository.findById(postId)
        .orElseThrow(() -> new EventException(NOT_FOUND_RECRUITMENT_POST));
    recruitmentPost.validateEvent(eventId);
    return RecruitmentPostResponse.from(recruitmentPost);
  }

  public List<RecruitmentPostQueryResponse> findRecruitmentPostsByMemberId(
      final Member member,
      final Long memberId
  ) {
    validateOwner(member, memberId);

    final List<RecruitmentPost> posts = recruitmentPostRepository.findAllByMember(member);

    return posts.stream()
        .map(RecruitmentPostQueryResponse::from)
        .collect(Collectors.toList());
  }

  private void validateOwner(final Member member, final Long memberId) {
    if (member.isNotMe(memberId)) {
      throw new MemberException(MemberExceptionType.NOT_MATCHING_TOKEN_AND_LOGIN_MEMBER);
    }
  }
}
