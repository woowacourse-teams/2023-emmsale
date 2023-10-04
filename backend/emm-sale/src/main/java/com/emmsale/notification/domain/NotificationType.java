package com.emmsale.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationType {

  COMMENT("comment"),
  EVENT("event");

  private final String type;
}
