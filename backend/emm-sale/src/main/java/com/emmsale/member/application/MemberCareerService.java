package com.emmsale.member.application;

import com.emmsale.career.domain.CareerRepository;
import com.emmsale.member.application.dto.MemberCareerInitialRequest;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberCareer;
import com.emmsale.member.domain.MemberCareerRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCareerService {

  private final MemberCareerRepository memberCareerRepository;
  private final CareerRepository careerRepository;

  public void registerCareer(final Member member, final MemberCareerInitialRequest memberCareerInitialRequest) {
    final List<Long> careerIds = memberCareerInitialRequest.getCareerIds();

    final List<MemberCareer> memberCareers = careerRepository.findAllById(careerIds)
        .stream()
        .map(it -> new MemberCareer(it, member))
        .collect(Collectors.toList());

    member.updateName(memberCareerInitialRequest.getName());

    memberCareerRepository.saveAll(memberCareers);
  }
}

