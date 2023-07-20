package com.emmsale.login.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberQueryResponse {

  private long memberId;
  private boolean isNewMember;
}
