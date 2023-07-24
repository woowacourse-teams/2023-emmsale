package com.emmsale.member.application;

import static java.util.stream.Collectors.toList;

import com.emmsale.activity.domain.ActivityRepository;
import com.emmsale.member.application.dto.MemberActivityAddRequest;
import com.emmsale.member.application.dto.MemberActivityDeleteRequest;
import com.emmsale.member.application.dto.MemberActivityInitialRequest;
import com.emmsale.member.application.dto.MemberActivityResponses;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberActivity;
import com.emmsale.member.domain.MemberActivityRepository;
import com.emmsale.member.exception.MemberException;
import com.emmsale.member.exception.MemberExceptionType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberActivityService {

  private final MemberActivityRepository memberActivityRepository;
  private final ActivityRepository activityRepository;

  public void registerCareer(
      final Member member,
      final MemberActivityInitialRequest memberActivityInitialRequest
  ) {
    final List<Long> careerIds = memberActivityInitialRequest.getActivityIds();
    saveMemberCareers(member, careerIds);

    member.updateName(memberActivityInitialRequest.getName());
  }

  private void saveMemberCareers(final Member member, final List<Long> careerIds) {
    final List<MemberActivity> memberActivities = activityRepository.findAllById(careerIds)
        .stream()
        .map(it -> new MemberActivity(it, member))
        .collect(toList());

    validateAllCareerIdsExist(careerIds, memberActivities);

    memberActivityRepository.saveAll(memberActivities);
  }

  private void validateAllCareerIdsExist(
      final List<Long> careerIds,
      final List<MemberActivity> memberActivities
  ) {
    if (memberActivities.size() != careerIds.size()) {
      throw new MemberException(MemberExceptionType.INVALID_CAREER_IDS);
    }
  }

  public List<MemberActivityResponses> addCareer(
      final Member member,
      final MemberActivityAddRequest memberActivityAddRequest
  ) {
    final List<Long> careerIds = memberActivityAddRequest.getActivityIds();
    saveMemberCareers(member, careerIds);

    return MemberActivityResponses.from(memberActivityRepository.findAllByMember(member));
  }

  public List<MemberActivityResponses> deleteCareer(
      final Member member,
      final MemberActivityDeleteRequest memberActivityDeleteRequest
  ) {
    final List<Long> deleteCareerIds = memberActivityDeleteRequest.getActivityIds();

    final List<Long> savedMemberCareerIds =
        memberActivityRepository.findAllByMemberAndCareerIds(member, deleteCareerIds)
            .stream()
            .map(MemberActivity::getId)
            .collect(toList());

    memberActivityRepository.deleteAllByIdInBatch(savedMemberCareerIds);

    return MemberActivityResponses.from(memberActivityRepository.findAllByMember(member));
  }

  @Transactional(readOnly = true)
  public List<MemberActivityResponses> findCareers(final Member member) {
    return MemberActivityResponses.from(memberActivityRepository.findAllByMember(member));
  }
}

