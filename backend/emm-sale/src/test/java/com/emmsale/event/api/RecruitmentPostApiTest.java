package com.emmsale.event.api;

import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.event.application.RecruitmentPostCommandService;
import com.emmsale.event.application.RecruitmentPostQueryService;
import com.emmsale.event.application.dto.RecruitmentPostRequest;
import com.emmsale.event.application.dto.RecruitmentPostResponse;
import com.emmsale.event.application.dto.RecruitmentPostUpdateRequest;
import com.emmsale.helper.MockMvcTestHelper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

@WebMvcTest(RecruitmentPostApi.class)
class RecruitmentPostApiTest extends MockMvcTestHelper {

  @MockBean
  private RecruitmentPostQueryService postQueryService;
  @MockBean
  private RecruitmentPostCommandService postCommandService;

  @Test
  @DisplayName("Event에 참여게시글을 추가할 수 있다.")
  void createRecruitmentPost() throws Exception {
    //given
    final Long eventId = 1L;
    final Long memberId = 2L;
    final Long postId = 3L;
    final String content = "함께 해요 게시글의 내용";
    final RecruitmentPostRequest request = new RecruitmentPostRequest(memberId, content);
    final String fakeAccessToken = "Bearer accessToken";

    final RequestFieldsSnippet requestFields = requestFields(
        fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("멤버 식별자"),
        fieldWithPath("content").type(JsonFieldType.STRING)
            .description("함께 해요 게시글의 내용(공백 불가, 255자 최대)")
    );

    when(postCommandService.createRecruitmentPost(any(), any(), any())).thenReturn(postId);

    //when
    mockMvc.perform(
            post("/events/{eventId}/recruitment-post", eventId).header("Authorization", fakeAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location",
            format("/events/%s/recruitment-post/%s", eventId, postId)))
        .andDo(document("create-recruitment-post", requestFields));
  }

  @Test
  @DisplayName("함께해요 게시글을 삭제할 수 있다.")
  void deleteRecruitmentPost() throws Exception {
    //given
    final Long eventId = 1L;
    final Long recruitmentPostId = 2L;
    final String fakeAccessToken = "Bearer accessToken";

    //when & then
    mockMvc.perform(delete("/events/{event-id}/recruitment-post/{recruitment-post-id}",
            eventId, recruitmentPostId)
            .header("Authorization", fakeAccessToken))
        .andExpect(status().isNoContent())
        .andDo(document("delete-recruitment-post"));

    verify(postCommandService).deleteRecruitmentPost(any(), any(), any());
  }

  @Test
  @DisplayName("행사의 참여 게시글을 전체 조회할 수 있다.")
  void findRecruitmentPosts() throws Exception {
    //given
    final Long eventId = 1L;
    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("함께해요 게시글 식별자"),
        fieldWithPath("[].memberId").type(JsonFieldType.NUMBER).description("member의 식별자"),
        fieldWithPath("[].name").type(JsonFieldType.STRING).description("member 이름"),
        fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("프로필 이미지 url"),
        fieldWithPath("[].content").type(JsonFieldType.STRING).description("함께해요 게시글 내용"),
        fieldWithPath("[].updatedAt").type(JsonFieldType.STRING).description("함께해요 게시글 수정 날짜")
    );
    final List<RecruitmentPostResponse> responses = List.of(
        new RecruitmentPostResponse(1L, 1L, "스캇", "imageUrl", "저랑 같이 컨퍼런스 갈 사람",
            LocalDate.of(2023, 7, 15)),
        new RecruitmentPostResponse(2L, 2L, "홍실", "imageUrl", "스캇 말고 저랑 갈 사람",
            LocalDate.of(2023, 7, 22))
    );

    when(postQueryService.findRecruitmentPosts(eventId)).thenReturn(responses);

    //when && then
    mockMvc.perform(get(format("/events/%s/recruitment-post", eventId)))
        .andExpect(status().isOk())
        .andDo(document("find-recruitment-post", responseFields));
  }

  @Test
  @DisplayName("Event에 참여게시글을 수정할 수 있다.")
  void updateRecruitmentPost() throws Exception {
    //given
    final Long eventId = 1L;
    final Long recruitmentPostId = 3L;
    final String content = "함께 해요 게시글의 내용";
    final RecruitmentPostUpdateRequest request = new RecruitmentPostUpdateRequest(content);
    final String fakeAccessToken = "Bearer accessToken";

    final RequestFieldsSnippet requestFields = requestFields(
        fieldWithPath("content").type(JsonFieldType.STRING)
            .description("함께 해요 게시글의 내용(공백 불가, 255자 최대)")
    );

    //when
    mockMvc.perform(
            put("/events/{eventId}/recruitment-post/{recruitment-post-id}", eventId, recruitmentPostId)
                .header("Authorization", fakeAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("update-recruitment-post", requestFields));

    verify(postCommandService).updateRecruitmentPost(any(), any(), any(), any());
  }

  @Test
  @DisplayName("이미 Event에 멤버가 참여헀는지 확인할 수 있다.")
  void isAlreadyRecruitTest() throws Exception {
    //given
    final Long memberId = 2L;
    final Long eventId = 3L;
    given(postQueryService.isAlreadyRecruit(eventId, memberId)).willReturn(true);

    //when && then
    mockMvc.perform(
            get("/events/{eventId}/recruitment-post/already-recruitment?member-id={memberId}"
                , eventId, memberId)
        )
        .andExpect(status().isOk())
        .andDo(document("check-already-recruitment-post"));
  }
}
