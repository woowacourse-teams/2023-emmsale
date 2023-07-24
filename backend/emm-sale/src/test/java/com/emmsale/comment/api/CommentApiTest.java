package com.emmsale.comment.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.comment.application.CommentCommandService;
import com.emmsale.comment.application.dto.CommentAddRequest;
import com.emmsale.comment.application.dto.CommentResponse;
import com.emmsale.helper.MockMvcTestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

@WebMvcTest(CommentApi.class)
class CommentApiTest extends MockMvcTestHelper {

  @MockBean
  private CommentCommandService commentCommandService;

  private static final RequestFieldsSnippet REQUEST_FIELDS = requestFields(
      fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용"),
      fieldWithPath("eventId").type(JsonFieldType.NUMBER).description("댓글을 생성할 행사 id"),
      fieldWithPath("parentId").type(JsonFieldType.NUMBER)
          .description("대댓글일 경우 부모 댓글 id").optional()
  );

  private static final ResponseFieldsSnippet RESPONSE_FIELDS = responseFields(
      fieldWithPath("content").type(JsonFieldType.STRING).description("저장된 댓글 내용"),
      fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("저장된 댓글 id"),
      fieldWithPath("parentId").type(JsonFieldType.NUMBER).description("대댓글일 경우 부모 댓글 id")
          .optional(),
      fieldWithPath("eventId").type(JsonFieldType.NUMBER).description("행사 id")
  );

  @Test
  @DisplayName("create() : 댓글에 제대로 저장이 되면 200 OK를 반환해줄 수 있다.")
  void test_create() throws Exception {
    //given
    final String content = "내용";
    final CommentAddRequest request = new CommentAddRequest(content, 1L, null);
    final CommentResponse commentResponse = new CommentResponse("내용", 1L, 1L, 1L, false);

    when(commentCommandService.create(any(), any()))
        .thenReturn(commentResponse);

    //when & then
    mockMvc.perform(post("/comments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("add-comment", REQUEST_FIELDS, RESPONSE_FIELDS));
  }
}
