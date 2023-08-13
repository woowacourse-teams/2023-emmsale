package com.emmsale.notification.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.helper.MockMvcTestHelper;
import com.emmsale.notification.application.UpdateNotificationQueryService;
import com.emmsale.notification.application.dto.UpdateNotificationResponse;
import com.emmsale.notification.application.dto.UpdateNotificationResponse.CommentTypeNotification;
import com.emmsale.notification.domain.UpdateNotificationType;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.RequestParametersSnippet;

@WebMvcTest(UpdateNotificationApi.class)
class UpdateNotificationApiTest extends MockMvcTestHelper {

  @MockBean
  private UpdateNotificationQueryService updateNotificationQueryService;

  @Test
  @DisplayName("find() : 현재 로그인 한 사용자가 받은 댓글 & 행사 알림들을 성공적으로 조회한다면 200 OK를 반환할 수 있다.")
  void test_find() throws Exception {
    //given
    final String accessToken = "Bearer Token";

    final RequestParametersSnippet requestParam = requestParameters(
        parameterWithName("member-id").description("알림을 조회할 멤버 ID"));

    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("[].updateNotificationId").description("알림 ID"),
        fieldWithPath("[].receiverId").description("수신자 ID"),
        fieldWithPath("[].redirectId").description("리다이렉션 ID"),
        fieldWithPath("[].createdAt").description("알림 생성 날짜"),
        fieldWithPath("[].type").description("알림 타입"),
        fieldWithPath("[].isRead").description("읽음 상태 유무"),
        fieldWithPath("[].commentTypeNotification").description("행사 알림일 경우 null, 댓글 알림일 경우 내용 존재").optional(),
        fieldWithPath("[].commentTypeNotification.content").description("(댓글 알림일 경우) 댓글 내용")
            .optional(),
        fieldWithPath("[].commentTypeNotification.eventName").description("(댓글 알림일 경우)이벤트 이름")
            .optional(),
        fieldWithPath("[].commentTypeNotification.commenterImageUrl").description("(댓글 알림일 경우) 댓글 작성자 이미지 Url")
            .optional()
    );

    final CommentTypeNotification commentTypeNotification = new CommentTypeNotification(
        "대댓글 내용",
        "이벤트 이름",
        "대댓글 단 사용자의 이미지 URL"
    );

    final List<UpdateNotificationResponse> responses = List.of(
        new UpdateNotificationResponse(
            1L, 2L,
            3L, LocalDateTime.now(),
            UpdateNotificationType.EVENT, true,
            null),
        new UpdateNotificationResponse(
            1L, 2L,
            3L, LocalDateTime.now(),
            UpdateNotificationType.COMMENT, false,
            commentTypeNotification
        )
    );

    //when
    when(updateNotificationQueryService.findAll(any(), anyLong()))
        .thenReturn(responses);

    //then
    mockMvc.perform(get("/update-notifications")
            .queryParam("member-id", "1")
            .header("Authorization", accessToken))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("get-update-notifications", requestParam, responseFields));
  }
}
