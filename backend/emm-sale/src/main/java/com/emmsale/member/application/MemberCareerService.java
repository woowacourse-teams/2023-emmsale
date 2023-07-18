package com.emmsale.member.application;

import static java.util.stream.Collectors.toList;

import com.emmsale.career.domain.CareerRepository;
import com.emmsale.member.application.dto.MemberCareerInitialRequest;
import com.emmsale.member.application.dto.MemberCareerRequest;
import com.emmsale.member.application.dto.MemberCareerResponse;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberCareer;
import com.emmsale.member.domain.MemberCareerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCareerService {

  private final MemberCareerRepository memberCareerRepository;
  private final CareerRepository careerRepository;

  public void registerCareer(
      final Member member,
      final MemberCareerInitialRequest memberCareerInitialRequest
  ) {
    final List<Long> careerIds = memberCareerInitialRequest.getCareerIds();
    saveMemberCareers(member, careerIds);

    member.updateName(memberCareerInitialRequest.getName());
  }

  private void saveMemberCareers(final Member member, final List<Long> careerIds) {
    final List<MemberCareer> memberCareers = careerRepository.findAllById(careerIds)
        .stream()
        .map(it -> new MemberCareer(it, member))
        .collect(toList());
    memberCareerRepository.saveAll(memberCareers);
  }

  public List<MemberCareerResponse> addCareer(
      final Member member,
      final MemberCareerRequest memberCareerRequest
  ) {
    final List<Long> careerIds = memberCareerRequest.getCareerIds();
    saveMemberCareers(member, careerIds);

    return MemberCareerResponse.from(memberCareerRepository.findAllByMemberId(member.getId()));
  }
}

