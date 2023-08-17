package com.emmsale.member.application;

import com.emmsale.member.application.dto.InterestTagAddRequest;
import com.emmsale.member.application.dto.InterestTagDeleteRequest;
import com.emmsale.member.application.dto.InterestTagResponse;
import com.emmsale.member.application.dto.InterestTagUpdateRequest;
import com.emmsale.member.domain.InterestTag;
import com.emmsale.member.domain.InterestTagRepository;
import com.emmsale.member.domain.Member;
import com.emmsale.member.exception.MemberException;
import com.emmsale.member.exception.MemberExceptionType;
import com.emmsale.tag.domain.Tag;
import com.emmsale.tag.domain.TagRepository;
import com.emmsale.tag.exception.TagException;
import com.emmsale.tag.exception.TagExceptionType;
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
    return InterestTagResponse.convertAllFrom(interestTags);
  }

  public List<InterestTagResponse> addInterestTag(final Member member,
      final InterestTagAddRequest request) {
    final List<Long> tagIds = request.getTagIds();
    final List<Tag> tags = tagRepository.findAllById(tagIds);
    validateAllTagExist(tags, tagIds);
    validateAlreadyExist(tagIds);
    final List<InterestTag> interestTags = tags
        .stream()
        .map(tag -> new InterestTag(member, tag))
        .collect(Collectors.toList());

    interestTagRepository.saveAll(interestTags);

    return InterestTagResponse.convertAllFrom(
        interestTagRepository.findInterestTagsByMemberId(member.getId()));
  }

  private void validateAllTagExist(final List<Tag> tags, final List<Long> tagIds) {
    if (tags.size() != tagIds.size()) {
      throw new TagException(TagExceptionType.NOT_FOUND_TAG);
    }
  }

  private void validateAlreadyExist(final List<Long> tagIds) {
    if (interestTagRepository.existsByTagIdIn(tagIds)) {
      throw new MemberException(MemberExceptionType.ALREADY_EXIST_INTEREST_TAG);
    }
  }


  public List<InterestTagResponse> deleteInterestTag(final Member member,
      final InterestTagDeleteRequest request) {
    final List<Long> tagIds = request.getTagIds();
    final List<InterestTag> interestTags = interestTagRepository.findAllByMemberAndTagIds(member,
        tagIds);
    final List<Tag> tags = tagRepository.findAllById(tagIds);
    validateAllTagExist(tags, tagIds);
    validateAllInterestTagExist(tagIds, interestTags);

    final List<Long> savedInterestTagIds = interestTags.stream()
        .map(InterestTag::getId)
        .collect(Collectors.toList());
    interestTagRepository.deleteAllByIdInBatch(savedInterestTagIds);
    return InterestTagResponse.convertAllFrom(
        interestTagRepository.findInterestTagsByMemberId(member.getId()));
  }

  private void validateAllInterestTagExist(final List<Long> tagIds,
      final List<InterestTag> interestTags) {
    if (interestTags.size() != tagIds.size()) {
      throw new MemberException(MemberExceptionType.NOT_FOUND_INTEREST_TAG);
    }
  }

  public List<InterestTagResponse> updateInterestTags(
      final Member member,
      final InterestTagUpdateRequest request
  ) {
    final List<Long> requestTagIds = request.getTagIds();
    final List<Tag> requestTags = tagRepository.findAllById(requestTagIds);

    validateAllTagExist(requestTags, requestTagIds);

    interestTagRepository.deleteAllByMember(member);

    final List<InterestTag> interestTags = requestTags.stream()
        .map(tag -> new InterestTag(member, tag))
        .collect(Collectors.toList());

    return interestTagRepository.saveAll(interestTags).stream()
        .map(InterestTagResponse::from)
        .collect(Collectors.toList());
  }
}
