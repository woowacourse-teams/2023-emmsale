package com.emmsale.notification.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.helper.MockMvcTestHelper;
import com.emmsale.notification.application.NotificationCommandService;
import com.emmsale.notification.application.NotificationQueryService;
import com.emmsale.notification.application.dto.FcmTokenRequest;
import com.emmsale.notification.application.dto.NotificationModifyRequest;
import com.emmsale.notification.application.dto.NotificationRequest;
import com.emmsale.notification.application.dto.NotificationResponse;
import com.emmsale.notification.domain.NotificationStatus;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;

@WebMvcTest(NotificationApi.class)
class NotificationApiTest extends MockMvcTestHelper {

  @MockBean
  private NotificationCommandService notificationCommandService;
  @MockBean
  private NotificationQueryService notificationQueryService;

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
    final long notificationId = 1L;

    final NotificationResponse response = new NotificationResponse(
        notificationId,
        senderId,
        receiverId,
        message,
        eventId
    );

    final NotificationRequest request = new NotificationRequest(
        senderId,
        receiverId,
        message,
        eventId
    );

    when(notificationCommandService.create(any()))
        .thenReturn(response);

    //when & then
    mockMvc.perform(post("/notifications")
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
    mockMvc.perform(post("/notifications/token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("create-fcmToken", requestFields));
  }

  @Test
  @DisplayName("modify() : 알림 상태를 성공적으로 변경되면 204 No Content를 반환할 수 있다.")
  void test_modify() throws Exception {
    //given
    final NotificationModifyRequest request =
        new NotificationModifyRequest(NotificationStatus.IN_PROGRESS);

    final RequestFieldsSnippet requestFields = requestFields(
        fieldWithPath("updatedStatus").description("변화시킬 상태(ACCEPTED 또는 REJECTED)")
    );

    final PathParametersSnippet pathParameters = pathParameters(
        parameterWithName("notification-id").description("상태변화 시킬 알림 ID")
    );

    final long notificationId = 1L;

    doNothing().when(notificationCommandService).modify(request, 3L);

    //when & then
    mockMvc.perform(patch("/notifications/{notification-id}", notificationId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent())
        .andDo(print())
        .andDo(document("modify-notification", requestFields, pathParameters));
  }

  @Test
  @DisplayName("find() : 알림 id를 통해 성공적으로 조회할 수 있다면 200 OK 를 반환할 수 있다.")
  void test_find() throws Exception {
    //given
    final PathParametersSnippet pathParameters = pathParameters(
        parameterWithName("notification-id").description("상태변화 시킬 알림 ID")
    );

    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("notificationId").description("저장된 알림 ID"),
        fieldWithPath("senderId").description("보내는 사람 ID"),
        fieldWithPath("receiverId").description("받는 사람 ID"),
        fieldWithPath("message").description("알림 보낼 때 메시지"),
        fieldWithPath("eventId").description("행사 ID")
    );

    final long senderId = 1L;
    final long receiverId = 2L;
    final long eventId = 3L;
    final String message = "알림 메시지야";
    final long notificationId = 1L;

    final NotificationResponse response = new NotificationResponse(
        notificationId,
        senderId,
        receiverId,
        message,
        eventId
    );

    //when
    when(notificationQueryService.findNotificationBy(anyLong()))
        .thenReturn(response);

    //then
    mockMvc.perform(get("/notifications/{notification-id}", notificationId))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("find-notification", responseFields, pathParameters));
  }

  @Test
  @DisplayName("사용자가 받은 알림의 목록을 성공적으로 반환하면 200 OK를 반환한다.")
  void test_findAll() throws Exception {
    //given
    final String accessToken = "Bearer access_token";

    final long memberId = 1L;

    final List<NotificationResponse> expectResponses = List.of(
        new NotificationResponse(931L, 3342L, memberId, "같이 가요~", 312L),
        new NotificationResponse(932L, 1345L, memberId, "소통해요~", 123L)
    );

    when(notificationCommandService.findAllNotifications(any())).thenReturn(expectResponses);

    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("[].notificationId").description("저장된 알림 ID"),
        fieldWithPath("[].senderId").description("보내는 사람 ID"),
        fieldWithPath("[].receiverId").description("받는 사람 ID"),
        fieldWithPath("[].message").description("알림 보낼 때 메시지"),
        fieldWithPath("[].eventId").description("행사 ID")
    );

    //when & then
    mockMvc.perform(get("/notifications")
            .header("Authorization", accessToken))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("find-all-notifications", responseFields));
  }

  @Test
  @DisplayName("delete() : 알림 상태를 성공적으로 변경되면 204 No Content를 반환할 수 있다.")
  void test_delete() throws Exception {
    //given
    final PathParametersSnippet pathParameters = pathParameters(
        parameterWithName("notification-id").description("삭제할 알림 ID")
    );

    final long notificationId = 1L;

    doNothing().when(notificationCommandService).delete(any(), any());

    //when & then
    mockMvc.perform(delete("/notifications/{notification-id}", notificationId)
            .header("Authorization", "Bearer AccessToken")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent())
        .andDo(print())
        .andDo(document("delete-notification", pathParameters));
  }
}
