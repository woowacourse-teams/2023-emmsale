package com.emmsale.notification.application.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class RequestNotificationExistedRequest {

  @NotNull
  private final Long receiverId;
  @NotNull
  private final Long senderId;
  @NotNull
  private final Long eventId;
}
