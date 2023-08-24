package com.emmsale.comment.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.comment.application.dto.CommentAddRequest;
import com.emmsale.comment.application.dto.CommentHierarchyResponse;
import com.emmsale.comment.application.dto.CommentModifyRequest;
import com.emmsale.comment.application.dto.CommentResponse;
import com.emmsale.helper.MockMvcTestHelper;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.restdocs.request.RequestParametersSnippet;

@WebMvcTest(CommentApi.class)
class CommentApiTest extends MockMvcTestHelper {

  private static final RequestFieldsSnippet REQUEST_FIELDS = requestFields(
      fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용"),
      fieldWithPath("eventId").type(JsonFieldType.NUMBER).description("댓글을 생성할 행사 id"),
      fieldWithPath("parentId").type(JsonFieldType.NUMBER)
          .description("대댓글일 경우 부모 댓글 id (부모 댓글일 경우 id null)").optional()
  );
  private static final ResponseFieldsSnippet RESPONSE_FIELDS = responseFields(
      fieldWithPath("content").description("저장된 댓글 내용"),
      fieldWithPath("commentId").description("저장된 댓글 id"),
      fieldWithPath("parentId").description("대댓글일 경우 부모 댓글 id").optional(),
      fieldWithPath("eventId").description("행사 id"),
      fieldWithPath("eventName").description("행사 이름"),
      fieldWithPath("createdAt").description("댓글 생성 시간"),
      fieldWithPath("updatedAt").description("댓글 최근 수정 시간"),
      fieldWithPath("deleted").description("댓글 삭제 여부"),
      fieldWithPath("memberId").description("댓글 작성자 ID"),
      fieldWithPath("memberImageUrl").description("댓글 작성자 이미지 Url"),
      fieldWithPath("memberName").description("댓글 작성자 이름")
  );

  @Test
  @DisplayName("create() : 댓글에 제대로 저장이 되면 200 OK를 반환해줄 수 있다.")
  void test_create() throws Exception {
    //given
    final String content = "내용";
    final CommentAddRequest request = new CommentAddRequest(content, 1L, null);

    final CommentResponse commentResponse = new CommentResponse(content, 2L, 1L, 1L, "eventName",
        false,
        LocalDateTime.now(), LocalDateTime.now(), 1L, "이미지", "이름1");

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

  @Test
  @DisplayName("findAll() : 행사에 있는 모든 댓글을 성공적으로 조회하면 200 OK를 반환할 수 있다.")
  void test_findAll() throws Exception {
    //given
    final String accessToken = "Bearer AccessToken";

    final RequestParametersSnippet requestParam = requestParameters(
        parameterWithName("eventId").description("댓글을 볼 행사 id(빈 값 가능)").optional(),
        parameterWithName("memberId").description("조회할 댓글을 작성한 사용자 ID(빈 값 가능) [둘 다 빈 값 X]")
            .optional()
    );

    final ResponseFieldsSnippet responseFieldsSnippet = responseFields(
        fieldWithPath("[].parentComment.content").description("댓글 내용"),
        fieldWithPath("[].parentComment.commentId").description("댓글 ID"),
        fieldWithPath("[].parentComment.parentId").description("부모 댓글 ID").optional(),
        fieldWithPath("[].parentComment.eventId").description("행사 ID"),
        fieldWithPath("[].parentComment.eventName").description("행사 이름"),
        fieldWithPath("[].parentComment.deleted").description("댓글 삭제 여부"),
        fieldWithPath("[].parentComment.createdAt").description("댓글 생성 날짜"),
        fieldWithPath("[].parentComment.updatedAt").description("댓글 수정 날짜"),
        fieldWithPath("[].parentComment.memberId").description("댓글 작성자 ID"),
        fieldWithPath("[].parentComment.memberImageUrl").description("댓글 작성자 이미지 Url"),
        fieldWithPath("[].parentComment.memberName").description("댓글 작성자 이름"),
        fieldWithPath("[].childComments[]").description("자식 댓글 목록"),
        fieldWithPath("[].childComments[].content").description("댓글 내용"),
        fieldWithPath("[].childComments[].commentId").description("댓글 ID"),
        fieldWithPath("[].childComments[].parentId").description("부모 댓글 ID").optional(),
        fieldWithPath("[].childComments[].eventId").description("행사 ID"),
        fieldWithPath("[].childComments[].eventName").description("행사 이름"),
        fieldWithPath("[].childComments[].deleted").description("댓글 삭제 여부"),
        fieldWithPath("[].childComments[].createdAt").description("댓글 생성 날짜"),
        fieldWithPath("[].childComments[].updatedAt").description("댓글 수정 날짜"),
        fieldWithPath("[].childComments[].memberId").description("댓글 작성자 ID"),
        fieldWithPath("[].childComments[].memberImageUrl").description("댓글 작성자 이미지 Url"),
        fieldWithPath("[].childComments[].memberName").description("댓글 작성자 이름")
    );

    final List<CommentHierarchyResponse> result = List.of(
        new CommentHierarchyResponse(
            new CommentResponse("부모댓글2", 4L, null, 1L, "eventName", false,
                LocalDateTime.now(), LocalDateTime.now(), 1L, "이미지", "이름1"),
            Collections.emptyList()
        ),
        new CommentHierarchyResponse(
            new CommentResponse("부모댓글1", 5L, null, 1L, "eventName", false,
                LocalDateTime.now(), LocalDateTime.now(), 1L, "이미지", "이름1"),
            List.of(
                new CommentResponse("부모댓글1에 대한 자식댓글1", 2L, 1L, 1L, "eventName", false,
                    LocalDateTime.now(), LocalDateTime.now(), 1L, "이미지", "이름1"),
                new CommentResponse("부모댓글1에 대한 자식댓글2", 3L, 1L, 1L, "eventName", false,
                    LocalDateTime.now(), LocalDateTime.now(), 1L, "이미지", "이름1"
                ))
        ));

    when(commentQueryService.findAllComments(any(), any()))
        .thenReturn(result);

    //when & then
    mockMvc.perform(get("/comments")
            .queryParam("eventId", "1")
            .header("Authorization", accessToken))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("get-comments", requestParam, responseFieldsSnippet));
  }

  @Test
  @DisplayName("findParentWithChildren() : 부모, 자식 댓글들이 성공적으로 조회되면 200 OK 를 반환할 수 있다.")
  void test_findChildren() throws Exception {
    //given
    final PathParametersSnippet pathParams = pathParameters(
        parameterWithName("comment-id").description("댓글 ID")
    );

    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("parentComment.content").description("댓글 내용"),
        fieldWithPath("parentComment.commentId").description("댓글 ID"),
        fieldWithPath("parentComment.parentId").description("부모 댓글 ID").optional(),
        fieldWithPath("parentComment.eventId").description("행사 ID"),
        fieldWithPath("parentComment.eventName").description("행사 제목"),
        fieldWithPath("parentComment.deleted").description("댓글 삭제 여부"),
        fieldWithPath("parentComment.createdAt").description("댓글 생성 날짜"),
        fieldWithPath("parentComment.updatedAt").description("댓글 수정 날짜"),
        fieldWithPath("parentComment.memberId").description("댓글 작성자 ID"),
        fieldWithPath("parentComment.memberImageUrl").description("댓글 작성자 이미지 Url"),
        fieldWithPath("parentComment.memberName").description("댓글 작성자 이름"),
        fieldWithPath("childComments[]").description("자식 댓글 목록"),
        fieldWithPath("childComments[].content").description("댓글 내용"),
        fieldWithPath("childComments[].commentId").description("댓글 ID"),
        fieldWithPath("childComments[].parentId").description("부모 댓글 ID").optional(),
        fieldWithPath("childComments[].eventId").description("행사 ID"),
        fieldWithPath("childComments[].eventName").description("행사 이름"),
        fieldWithPath("childComments[].deleted").description("댓글 삭제 여부"),
        fieldWithPath("childComments[].createdAt").description("댓글 생성 날짜"),
        fieldWithPath("childComments[].updatedAt").description("댓글 수정 날짜"),
        fieldWithPath("childComments[].memberId").description("댓글 작성자 ID"),
        fieldWithPath("childComments[].memberImageUrl").description("댓글 작성자 이미지 Url"),
        fieldWithPath("childComments[].memberName").description("댓글 작성자 이름")
    );

    final CommentHierarchyResponse result =
        new CommentHierarchyResponse(
            new CommentResponse("부모댓글1", 5L, null, 1L, "eventName", false,
                LocalDateTime.now(), LocalDateTime.now(), 1L, "이미지", "이름1"),
            List.of(
                new CommentResponse("부모댓글1에 대한 자식댓글1", 2L, 1L, 1L, "eventName", false,
                    LocalDateTime.now(), LocalDateTime.now(), 1L, "이미지", "이름1"),
                new CommentResponse("부모댓글1에 대한 자식댓글2", 3L, 1L, 1L, "eventName", false,
                    LocalDateTime.now(), LocalDateTime.now(), 1L, "이미지", "이름1")
            )
        );

    //when
    when(commentQueryService.findParentWithChildren(anyLong(), any()))
        .thenReturn(result);

    //then
    mockMvc.perform(get("/comments/{comment-id}", 1L)
            .header("Authorization", "Bearer AccessToken"))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("get-children-comment", pathParams, responseFields));
  }

  @Test
  @DisplayName("delete() : 댓글이 정상적으로 삭제되면 204 No Content를 반환할 수 있다.")
  void test_delete() throws Exception {
    //given
    final PathParametersSnippet pathParams = pathParameters(
        parameterWithName("comment-id").description("삭제할 댓글의 ID")
    );

    //when & then
    mockMvc.perform(delete("/comments/{comment-id}", 1L))
        .andExpect(status().isNoContent())
        .andDo(print())
        .andDo(document("delete-comment", pathParams));
  }

  @Test
  @DisplayName("modify() : 댓글이 정상적으로 수정되면 200 OK 를 반환할 수 있다.")
  void test_modify() throws Exception {
    //given
    final String modifiedContent = "변경된 내용";
    final CommentModifyRequest request = new CommentModifyRequest(modifiedContent);

    final CommentResponse response = new CommentResponse("댓", 5L, null, 1L, "eventName", false,
        LocalDateTime.now(), LocalDateTime.now(), 1L, "이미지", "이름1");

    when(commentCommandService.modify(anyLong(), any(), any()))
        .thenReturn(response);

    final RequestFieldsSnippet requestFields = requestFields(
        fieldWithPath("content").type(JsonFieldType.STRING).description("변경할 댓글 내용")
    );

    final PathParametersSnippet pathParams = pathParameters(
        parameterWithName("comment-id").description("수정할 댓글의 ID")
    );

    //when & then
    mockMvc.perform(patch("/comments/{comment-id}", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("modify-comment", requestFields, pathParams, RESPONSE_FIELDS));
  }
}
