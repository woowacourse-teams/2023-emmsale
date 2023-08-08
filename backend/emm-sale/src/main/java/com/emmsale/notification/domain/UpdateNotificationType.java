package com.emmsale.notification.domain;

import static com.emmsale.notification.exception.NotificationExceptionType.NOT_FOUND_NOTIFICATION_TYPE;

import com.emmsale.comment.domain.Comment;
import com.emmsale.event.domain.Event;
import com.emmsale.notification.exception.NotificationException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UpdateNotificationType {

  COMMENT(Comment.class),
  EVENT(Event.class);

  private final Class<?> classType;

  public static UpdateNotificationType from(final Class<?> classType) {
    return Arrays.stream(values())
        .filter(it -> it.classType.isAssignableFrom(classType))
        .findAny()
        .orElseThrow(() -> new NotificationException(NOT_FOUND_NOTIFICATION_TYPE));
  }
}
