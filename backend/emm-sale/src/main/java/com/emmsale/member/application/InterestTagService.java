package com.emmsale.member.application;

import com.emmsale.member.application.dto.InterestTagRequest;
import com.emmsale.member.application.dto.InterestTagResponse;
import com.emmsale.member.domain.InterestTag;
import com.emmsale.member.domain.InterestTagRepository;
import com.emmsale.member.domain.Member;
import com.emmsale.tag.domain.TagRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class InterestTagService {

  private final InterestTagRepository interestTagRepository;
  private final TagRepository tagRepository;

  public List<InterestTagResponse> findInterestTags(final Long memberId) {
    final List<InterestTag> interestTags = interestTagRepository.findInterestTagsByMemberId(
        memberId);
    return InterestTagResponse.from(interestTags);
  }

  public List<InterestTagResponse> addInterestTag(final Member member,
      final InterestTagRequest request) {
    // TODO: 2023-08-10 유효성 검증
    final List<InterestTag> interestTags = tagRepository.findAllById(request.getTagIds())
        .stream()
        .map(tag -> new InterestTag(member, tag))
        .collect(Collectors.toList());

    interestTagRepository.saveAll(interestTags);

    return InterestTagResponse.from(
        interestTagRepository.findInterestTagsByMemberId(member.getId()));
  }

  public List<InterestTagResponse> deleteInterestTag(final Member member,
      final InterestTagRequest request) {

    final List<Long> savedInterestTagIds = interestTagRepository.findAllByMemberAndTagIds(member,
            request.getTagIds())
        .stream()
        .map(InterestTag::getId)
        .collect(Collectors.toList());
    interestTagRepository.deleteAllByIdInBatch(savedInterestTagIds);
    return InterestTagResponse.from(
        interestTagRepository.findInterestTagsByMemberId(member.getId()));
  }


}
