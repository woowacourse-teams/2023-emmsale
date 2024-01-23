package com.emmsale.member.application;

import static java.util.stream.Collectors.toUnmodifiableList;

import com.emmsale.activity.application.dto.ActivityResponse;
import com.emmsale.activity.domain.ActivityRepository;
import com.emmsale.member.application.dto.MemberActivityAddRequest;
import com.emmsale.member.application.dto.MemberActivityInitialRequest;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberActivity;
import com.emmsale.member.domain.MemberActivityRepository;
import com.emmsale.member.exception.MemberException;
import com.emmsale.member.exception.MemberExceptionType;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberActivityCommandService {

  private final MemberActivityRepository memberActivityRepository;
  private final ActivityRepository activityRepository;

  public List<MemberActivity> registerActivities(
      final Member member,
      final MemberActivityInitialRequest memberActivityInitialRequest
  ) {
    if (member.isOnboarded()) {
      throw new MemberException(MemberExceptionType.ALREADY_ONBOARDING);
    }
    final List<Long> activityIds = memberActivityInitialRequest.getActivityIds();
    return saveMemberActivities(member, activityIds);
  }

  private List<MemberActivity> saveMemberActivities(final Member member,
      final List<Long> activityIds) {
    final List<MemberActivity> memberActivities = activityRepository.findAllById(activityIds)
        .stream()
        .map(it -> new MemberActivity(it, member))
        .collect(toUnmodifiableList());

    validateAllActivityIdsExist(activityIds, memberActivities);
    memberActivityRepository.saveAll(memberActivities);
    return memberActivities;
  }

  private void validateAllActivityIdsExist(
      final List<Long> activityIds,
      final List<MemberActivity> memberActivities
  ) {
    if (memberActivities.size() != activityIds.size()) {
      throw new MemberException(MemberExceptionType.INVALID_ACTIVITY_IDS);
    }
  }

  public List<ActivityResponse> addActivity(
      final Member member,
      final MemberActivityAddRequest memberActivityAddRequest
  ) {
    final List<Long> activityIds = memberActivityAddRequest.getActivityIds();
    final List<MemberActivity> memberActivities = memberActivityRepository.findAllByMember(member);
    if (hasDuplicateId(activityIds)) {
      throw new MemberException(MemberExceptionType.DUPLICATE_ACTIVITY);
    }
    if (isAlreadyExistActivity(memberActivities, activityIds)) {
      throw new MemberException(MemberExceptionType.ALREADY_EXIST_ACTIVITY);
    }
    saveMemberActivities(member, activityIds);

    return memberActivityRepository.findAllByMember(member)
        .stream()
        .map(MemberActivity::getActivity)
        .map(ActivityResponse::from)
        .collect(toUnmodifiableList());
  }

  private boolean isAlreadyExistActivity(final List<MemberActivity> memberActivities,
      final List<Long> activityIds) {
    return memberActivities
        .stream()
        .anyMatch(memberActivity ->
            activityIds.contains(memberActivity.getActivity().getId())
        );
  }

  private boolean hasDuplicateId(final List<Long> activityIds) {
    return new HashSet<>(activityIds).size() != activityIds.size();
  }

  public List<ActivityResponse> deleteActivity(
      final Member member,
      final List<Long> deleteActivityIds
  ) {
    final List<Long> savedMemberActivityIds =
        memberActivityRepository.findAllByMemberAndActivityIds(member, deleteActivityIds)
            .stream()
            .map(MemberActivity::getId)
            .collect(toUnmodifiableList());

    memberActivityRepository.deleteAllByIdInBatch(savedMemberActivityIds);

    return memberActivityRepository.findAllByMember(member)
        .stream()
        .map(MemberActivity::getActivity)
        .map(ActivityResponse::from)
        .collect(toUnmodifiableList());
  }
}
