package com.emmsale.notification.application.dto;

import com.emmsale.notification.domain.Notification;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CommentNotificationMessage {

  @JsonProperty("validate_only")
  private final boolean validateOnly;
  private final Message message;

  @RequiredArgsConstructor
  @Getter
  public static class Message {

    private final Data data;
    private final String token;
  }

  @RequiredArgsConstructor
  @Getter
  public static class Data {

    private final String receiverId;
    private final String redirectId;
    private final String notificationType;
    private final String createdAt;
    private final String content;
    private final String writer;
    private final String writerImageUrl;

    public static Data from(final Notification notification) {
      final JsonObject jsonData = JsonParser.parseString(notification.getJsonData())
          .getAsJsonObject();

      return new Data(
          notification.getReceiverId().toString(),
          notification.getRedirectId().toString(),
          notification.getType().name(),
          notification.getCreatedAt().toString(),
          jsonData.get("content").getAsString(),
          jsonData.get("writer").getAsString(),
          jsonData.get("writerImageUrl").getAsString()
      );
    }
  }
}
