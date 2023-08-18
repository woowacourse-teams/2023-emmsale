package com.emmsale.comment.application.dto;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class CommentFindRequest {

  private final Long eventId;
  @Getter(AccessLevel.PRIVATE)
  private final Long memberId;

  public Optional<Long> getOptionalMemberId() {
    return Optional.ofNullable(memberId);
  }
}
