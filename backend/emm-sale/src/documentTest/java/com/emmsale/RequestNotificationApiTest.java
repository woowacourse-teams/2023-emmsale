package com.emmsale;

import static com.emmsale.notification.exception.NotificationExceptionType.NO_CONTENT_BLOCKED_MEMBER;
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
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.notification.api.RequestNotificationApi;
import com.emmsale.notification.application.dto.RequestNotificationModifyRequest;
import com.emmsale.notification.application.dto.RequestNotificationRequest;
import com.emmsale.notification.application.dto.RequestNotificationResponse;
import com.emmsale.notification.domain.RequestNotificationStatus;
import com.emmsale.notification.exception.NotificationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.restdocs.request.RequestParametersSnippet;

@WebMvcTest(RequestNotificationApi.class)
class RequestNotificationApiTest extends MockMvcTestHelper {


  private static final ResponseFieldsSnippet RESPONSE_FIELDS = responseFields(
      fieldWithPath("notificationId").description("저장된 알림 ID"),
      fieldWithPath("senderId").description("보내는 사람 ID"),
      fieldWithPath("receiverId").description("받는 사람 ID"),
      fieldWithPath("message").description("알림 보낼 때 메시지"),
      fieldWithPath("eventId").description("행사 ID"),
      fieldWithPath("isRead").description("읽은 상태"),
      fieldWithPath("status").description("ACCEPTED/REJECTED/IN_PROGRESS 상태"),
      fieldWithPath("createdAt").description("알림 생성 시간")
  );
  private static final RequestFieldsSnippet REQUEST_FIELDS = requestFields(
      fieldWithPath("senderId").description("보내는 사람 ID"),
      fieldWithPath("receiverId").description("받는 사람 ID"),
      fieldWithPath("message").description("알림 보낼 때 메시지"),
      fieldWithPath("eventId").description("행사 ID")
  );

  @Test
  @DisplayName("create() : 알림을 성공적으로 생성한다면 201 Created를 반환할 수 있다.")
  void test_create() throws Exception {
    //given

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

    final RequestNotificationResponse response = new RequestNotificationResponse(
        notificationId,
        senderId,
        receiverId,
        message,
        eventId,
        false,
        RequestNotificationStatus.IN_PROGRESS,
        LocalDateTime.now()
    );

    final RequestNotificationRequest request = new RequestNotificationRequest(
        senderId,
        receiverId,
        message,
        eventId
    );

    when(requestNotificationCommandService.create(any()))
        .thenReturn(Optional.of(response));

    //when & then
    mockMvc.perform(post("/request-notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(print())
        .andDo(document("create-request-notification", requestFields, RESPONSE_FIELDS));
  }

  @Test
  @DisplayName("create() : 차단된 사용자에게 알림을 보낼 경우 204 NO_CONTENT를 반환한다.")
  void test_createWithBlockedSender() throws Exception {
    //given
    final long senderId = 1L;
    final long receiverId = 2L;
    final long eventId = 3L;
    final String message = "알림 메시지야";

    final RequestNotificationRequest request = new RequestNotificationRequest(
        senderId,
        receiverId,
        message,
        eventId
    );

    when(requestNotificationCommandService.create(any()))
        .thenThrow(new NotificationException(NO_CONTENT_BLOCKED_MEMBER));

    //when & then
    mockMvc.perform(post("/request-notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent())
        .andDo(print())
        .andDo(document("create-blocked-member-requestNotification", REQUEST_FIELDS));
  }


  @Test
  @DisplayName("modify() : 알림 상태를 성공적으로 변경되면 204 No Content를 반환할 수 있다.")
  void test_modify() throws Exception {
    //given
    final RequestNotificationModifyRequest request =
        new RequestNotificationModifyRequest(RequestNotificationStatus.IN_PROGRESS);

    final RequestFieldsSnippet requestFields = requestFields(
        fieldWithPath("updatedStatus").description("변화시킬 상태(ACCEPTED 또는 REJECTED)")
    );

    final PathParametersSnippet pathParameters = pathParameters(
        parameterWithName("request-notification-id").description("상태변화 시킬 알림 ID")
    );

    final long notificationId = 1L;

    doNothing().when(requestNotificationCommandService).modify(request, 3L);

    //when & then
    mockMvc.perform(patch("/request-notifications/{request-notification-id}/status", notificationId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent())
        .andDo(print())
        .andDo(document("modify-request-notification", requestFields, pathParameters));
  }

  @Test
  @DisplayName("find() : 알림 id를 통해 성공적으로 조회할 수 있다면 200 OK 를 반환할 수 있다.")
  void test_find() throws Exception {
    //given
    final PathParametersSnippet pathParameters = pathParameters(
        parameterWithName("request-notification-id").description("상태변화 시킬 알림 ID")
    );

    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("notificationId").description("저장된 알림 ID"),
        fieldWithPath("senderId").description("보내는 사람 ID"),
        fieldWithPath("receiverId").description("받는 사람 ID"),
        fieldWithPath("message").description("알림 보낼 때 메시지"),
        fieldWithPath("eventId").description("행사 ID"),
        fieldWithPath("isRead").description("읽은 상태"),
        fieldWithPath("status").description("ACCEPTED/REJECTED/IN_PROGRESS 상태"),
        fieldWithPath("createdAt").description("알림 생성 시간")
    );

    final long senderId = 1L;
    final long receiverId = 2L;
    final long eventId = 3L;
    final String message = "알림 메시지야";
    final long notificationId = 1L;

    final RequestNotificationResponse response = new RequestNotificationResponse(
        notificationId,
        senderId,
        receiverId,
        message,
        eventId,
        true,
        RequestNotificationStatus.IN_PROGRESS,
        LocalDateTime.now()
    );

    //when
    when(requestNotificationQueryService.findNotificationBy(anyLong()))
        .thenReturn(response);

    //then
    mockMvc.perform(get("/request-notifications/{request-notification-id}", notificationId))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("find-request-notification", responseFields, pathParameters));
  }

  @Test
  @DisplayName("사용자가 받은 알림의 목록을 성공적으로 반환하면 200 OK를 반환한다.")
  void test_findAll() throws Exception {
    //given
    final String accessToken = "Bearer access_token";

    final long memberId = 1L;

    final List<RequestNotificationResponse> expectResponses = List.of(
        new RequestNotificationResponse(
            931L, 3342L,
            memberId, "같이 가요~",
            312L, false,
            RequestNotificationStatus.REJECTED,
            LocalDateTime.now()
        ),
        new RequestNotificationResponse(
            932L, 1345L,
            memberId, "소통해요~",
            123L, true,
            RequestNotificationStatus.ACCEPTED,
            LocalDateTime.now()
        )
    );

    when(requestNotificationCommandService.findAllNotifications(any()))
        .thenReturn(expectResponses);

    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("[].notificationId").description("저장된 알림 ID"),
        fieldWithPath("[].senderId").description("보내는 사람 ID"),
        fieldWithPath("[].receiverId").description("받는 사람 ID"),
        fieldWithPath("[].message").description("알림 보낼 때 메시지"),
        fieldWithPath("[].eventId").description("행사 ID"),
        fieldWithPath("[].isRead").description("읽은 상태"),
        fieldWithPath("[].status").description("ACCEPTED/REJECTED/IN_PROGRESS 상태"),
        fieldWithPath("[].createdAt").description("알림 생성 시간")
    );

    //when & then
    mockMvc.perform(get("/request-notifications")
            .header(HttpHeaders.AUTHORIZATION, accessToken))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("find-all-request-notification", responseFields));
  }

  @Test
  @DisplayName("delete() : 알림 상태를 성공적으로 변경되면 204 No Content를 반환할 수 있다.")
  void test_delete() throws Exception {
    //given
    final PathParametersSnippet pathParameters = pathParameters(
        parameterWithName("request-notification-id").description("삭제할 알림 ID")
    );

    final long notificationId = 1L;

    doNothing().when(requestNotificationCommandService).delete(any(), any());

    //when & then
    mockMvc.perform(delete("/request-notifications/{request-notification-id}", notificationId)
            .header(HttpHeaders.AUTHORIZATION, "Bearer AccessToken")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent())
        .andDo(print())
        .andDo(document("delete-request-notification", pathParameters));
  }

  @Test
  @DisplayName("read() : 알림의 읽음 상태가 성공적으로 읽은 상태가 되면 204 No Content를 반환할 수 있다.")
  void test_read() throws Exception {
    //given
    final PathParametersSnippet pathParameters = pathParameters(
        parameterWithName("request-notification-id").description("읽은 알림 ID")
    );

    final long notificationId = 1L;

    doNothing().when(requestNotificationCommandService).read(anyLong(), any());

    //when & then
    mockMvc.perform(patch("/request-notifications/{request-notification-id}/read", notificationId)
            .header(HttpHeaders.AUTHORIZATION, "Bearer AccessToken"))
        .andExpect(status().isNoContent())
        .andDo(print())
        .andDo(document("read-request-notification", pathParameters));
  }

  @Test
  @DisplayName("isAlreadyExisted() : 현재 사용자가 행사에서 같이 가기 요청을 이미 보냈는지 제대로 확인할 수 있다면 200 OK 를 반환할 수 있다.")
  void test_isAlreadyExisted() throws Exception {
    //given
    final String accessToken = "Bearer access_token";

    final RequestParametersSnippet requestParam = requestParameters(
        parameterWithName("receiverId").description("알림 받을 사람 id"),
        parameterWithName("senderId").description("알림 보낸 사람 id"),
        parameterWithName("eventId").description("행사 id")
    );

    when(requestNotificationQueryService.isAlreadyExisted(any(), any()))
        .thenReturn(true);

    //when & then
    mockMvc.perform(get("/request-notifications/existed")
            .queryParam("receiverId", "1")
            .queryParam("senderId", "2")
            .queryParam("eventId", "3")
            .header(HttpHeaders.AUTHORIZATION, accessToken))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("already-existed-request-notification", requestParam));
  }
}
