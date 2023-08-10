package com.emmsale.member.application;

import com.emmsale.member.application.dto.InterestTagRequest;
import com.emmsale.member.application.dto.InterestTagResponse;
import com.emmsale.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class InterestTagService {

  // TODO: 2023-08-10 고쳐야 함
  public List<InterestTagResponse> findInterestTags(final Long memberId) {
    return List.of(new InterestTagResponse(1L, "백엔드"),
        new InterestTagResponse(2L, "프론트엔드"),
        new InterestTagResponse(4L, "IOS"),
        new InterestTagResponse(5L, "AI"));
  }

  public List<InterestTagResponse> addInterestTag(final Member member,
      final InterestTagRequest request) {
    return List.of(new InterestTagResponse(1L, "백엔드"),
        new InterestTagResponse(2L, "프론트엔드"),
        new InterestTagResponse(4L, "IOS"),
        new InterestTagResponse(5L, "AI"));
  }

  public List<InterestTagResponse> deleteInterestTag(final Member member,
      final InterestTagRequest request) {
    return List.of(new InterestTagResponse(1L, "백엔드"),
        new InterestTagResponse(2L, "프론트엔드"),
        new InterestTagResponse(4L, "IOS"),
        new InterestTagResponse(5L, "AI"));
  }


}
