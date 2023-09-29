package com.emmsale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.notification.api.NotificationApi;
import com.emmsale.notification.application.dto.NotificationAllResponse;
import com.emmsale.notification.domain.NotificationType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.request.RequestParametersSnippet;

@WebMvcTest(NotificationApi.class)
class NotificationApiTest extends MockMvcTestHelper {

  private String commentJsonData1, commentJsonData2, eventJsonData1;

  @BeforeEach
  void setUp() {
    commentJsonData1 = "{"
        + "\"receiverId\":1,"
        + "\"redirectId\":5100,"
        + "\"createdAt\":\"2023-09-27T15:41:59.802027\","
        + "\"notificationType\":\"COMMENT\","
        + "\"content\":\"content\","
        + "\"writer\":\"writer\","
        + "\"writerImageUrl\":\"imageUrl\""
        + "}";

    eventJsonData1 = "{"
        + "\"receiverId\":1,"
        + "\"redirectId\":5101,"
        + "\"notificationType\":\"EVENT\","
        + "\"createdAt\":\"2023-09-27T15:41:59.802027\","
        + "\"title\":\"title\""
        + "}";

    commentJsonData2 = "{"
        + "\"receiverId\":2,"
        + "\"redirectId\":5102,"
        + "\"createdAt\":\"2023-09-27T15:41:59.802027\","
        + "\"notificationType\":\"COMMENT\","
        + "\"content\":\"content\","
        + "\"writer\":\"writer\","
        + "\"writerImageUrl\":\"imageUrl\""
        + "}";
  }

  @Test
  @DisplayName("find() : 현재 로그인 한 사용자가 받은 알림들을 성공적으로 조회한다면 200 OK를 반환할 수 있다.")
  void test_find() throws Exception {
    //given
    final String accessToken = "Bearer Token";

    final RequestParametersSnippet requestParam = requestParameters(
        RequestDocumentation.parameterWithName("member-id").description("알림을 조회할 멤버 ID"));

    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("[].notificationId").description("알림 ID"),
        fieldWithPath("[].type").description("알림 종류"),
        fieldWithPath("[].notificationInformation").description("알림 정보"),
        fieldWithPath("[].isRead").description("사용자가 알림을 읽었는지 유무")
    );

    final List<NotificationAllResponse> responses = List.of(
        new NotificationAllResponse(
            1L,
            NotificationType.COMMENT,
            commentJsonData1,
            false
        ),
        new NotificationAllResponse(
            1L,
            NotificationType.EVENT,
            eventJsonData1,
            false
        )
    );

    //when
    when(notificationQueryService.findAllByMemberId(any(), anyLong()))
        .thenReturn(responses);

    //then
    mockMvc.perform(get("/notifications")
            .queryParam("member-id", "1")
            .header("Authorization", accessToken))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("get-all-notifications", requestParam, responseFields));
  }
}
