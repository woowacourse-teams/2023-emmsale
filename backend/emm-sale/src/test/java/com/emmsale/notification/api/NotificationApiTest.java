package com.emmsale.notification.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.helper.MockMvcTestHelper;
import com.emmsale.notification.application.NotificationCommandService;
import com.emmsale.notification.application.dto.FcmTokenRequest;
import com.emmsale.notification.application.dto.NotificationRequest;
import com.emmsale.notification.application.dto.NotificationResponse;
import com.emmsale.notification.domain.Notification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

@WebMvcTest(NotificationApi.class)
class NotificationApiTest extends MockMvcTestHelper {

  @MockBean
  private NotificationCommandService notificationCommandService;

  @Test
  @DisplayName("create() : 알림을 성공적으로 생성한다면 201 Created를 반환할 수 있다.")
  void test_create() throws Exception {
    //given
    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("notificationId").description("저장된 알림 ID"),
        fieldWithPath("senderId").description("보내는 사람 ID"),
        fieldWithPath("receiverId").description("받는 사람 ID"),
        fieldWithPath("message").description("알림 보낼 때 메시지"),
        fieldWithPath("eventId").description("행사 ID")
    );

    final RequestFieldsSnippet requestFields = requestFields(
        fieldWithPath("senderId").description("보내는 사람 ID"),
        fieldWithPath("receiverId").description("받는 사람 ID"),
        fieldWithPath("message").description("알림 보낼 때 메시지"),
        fieldWithPath("eventId").description("행사 ID")
    );

    final long senderId = 1L;
    final long receiverId = 2L;
    final long eventId = 3L;
    final String message = "알림 메시지야";

    final Notification notification = new Notification(senderId, receiverId, eventId, message);
    final NotificationResponse response = NotificationResponse.from(notification);
    final NotificationRequest request = new NotificationRequest(
        senderId,
        receiverId,
        message,
        eventId
    );

    when(notificationCommandService.create(any()))
        .thenReturn(response);

    //when & then
    mockMvc.perform(post("/notification")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(print())
        .andDo(document("create-notification", requestFields, responseFields));
  }

  @Test
  @DisplayName("createFcmToken() : FcmToken이 새로 생성되거나 잘 수정되면 200 OK를 반환할 수 있다.")
  void test_createFcmToken() throws Exception {
    //given
    final RequestFieldsSnippet requestFields = requestFields(
        fieldWithPath("token").description("FcmToken 주세요"),
        fieldWithPath("memberId").description("FcmToken 주인의 ID")
    );

    final long memberId = 1L;
    final String token = "FCM 토큰";

    final FcmTokenRequest request = new FcmTokenRequest(token, memberId);

    //when & then
    mockMvc.perform(post("/notification/token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("create-fcmToken", requestFields));
  }
}
