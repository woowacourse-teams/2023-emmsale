package com.emmsale.member.application;

import com.emmsale.member.application.dto.OpenProfileUrlRequest;
import com.emmsale.member.domain.Member;
import org.springframework.stereotype.Service;

@Service
public class MemberUpdateService {

  public void updateOpenProfileUrl(
      final Member member,
      final OpenProfileUrlRequest openProfileUrlRequest
  ) {
    final String openProfileUrl = openProfileUrlRequest.getOpenProfileUrl();

    member.updateOpenProfileUrl(openProfileUrl);
  }
}
