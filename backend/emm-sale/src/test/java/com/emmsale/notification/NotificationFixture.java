package com.emmsale.notification;

public class NotificationFixture {

  public static String commentJsonData() {
    return "{"
        + "\"content\":\"댓글 내용\","
        + "\"writer\":\"댓글 작성한 유저 이름\","
        + "\"writerImageUrl\":\"댓글 작성한 유저 ImageUrl\","
        + "\"feedId\":\"댓글 달린 피드 ID\","
        + "\"parentCommentId\":\"대댓글 달린 부모 ID\""
        + "}";
  }

  public static String eventJsonData() {
    return "{"
        + "\"title\":\"title\""
        + "}";
  }
}
