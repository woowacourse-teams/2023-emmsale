package com.emmsale;

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

import com.emmsale.event.api.RecruitmentPostApi;
import com.emmsale.event.application.dto.RecruitmentPostQueryResponse;
import com.emmsale.event.application.dto.RecruitmentPostRequest;
import com.emmsale.event.application.dto.RecruitmentPostUpdateRequest;
import com.emmsale.member.application.dto.MemberReferenceResponse;
import com.emmsale.member.domain.Member;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

@WebMvcTest(RecruitmentPostApi.class)
class RecruitmentPostApiTest extends MockMvcTestHelper {

  private static final ResponseFieldsSnippet RECRUITMENT_POSTS_RESPONSE_FIELDS = responseFields(
      fieldWithPath("[].postId").type(JsonFieldType.NUMBER).description("함께해요 게시글 식별자"),
      fieldWithPath("[].content").type(JsonFieldType.STRING).description("함께해요 게시글 내용"),
      fieldWithPath("[].updatedAt").type(JsonFieldType.STRING).description("함께해요 게시글 수정 날짜"),
      fieldWithPath("[].member.id").type(JsonFieldType.NUMBER).description("member의 식별자"),
      fieldWithPath("[].member.name").type(JsonFieldType.STRING).description("member의 이름"),
      fieldWithPath("[].member.description").type(JsonFieldType.STRING)
          .description("member의 한줄 자기소개"),
      fieldWithPath("[].member.imageUrl").type(JsonFieldType.STRING)
          .description("member의 이미지 url"),
      fieldWithPath("[].member.githubUrl").type(JsonFieldType.STRING)
          .description("member의 github Url"),
      fieldWithPath("[].eventId").type(JsonFieldType.NUMBER).description("행사의 식별자"),
      fieldWithPath("[].eventName").type(JsonFieldType.STRING).description("행사의 이름")
  );

  private static final ResponseFieldsSnippet RECRUITMENT_POST_RESPONSE_FIELDS = responseFields(
      fieldWithPath("postId").type(JsonFieldType.NUMBER).description("함께해요 게시글 식별자"),
      fieldWithPath("content").type(JsonFieldType.STRING).description("함께해요 게시글 내용"),
      fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("함께해요 게시글 수정 날짜"),
      fieldWithPath("member.id").type(JsonFieldType.NUMBER).description("member의 식별자"),
      fieldWithPath("member.name").type(JsonFieldType.STRING).description("member의 이름"),
      fieldWithPath("member.description").type(JsonFieldType.STRING)
          .description("member의 한줄 자기소개"),
      fieldWithPath("member.imageUrl").type(JsonFieldType.STRING)
          .description("member의 이미지 url"),
      fieldWithPath("member.githubUrl").type(JsonFieldType.STRING)
          .description("member의 github Url"),
      fieldWithPath("eventId").type(JsonFieldType.NUMBER).description("행사의 식별자"),
      fieldWithPath("eventName").type(JsonFieldType.STRING).description("행사의 이름")
  );

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

    when(postCommandService.createRecruitmentPost(any(), any(), any()))
        .thenReturn(postId);

    //when
    mockMvc.perform(post("/events/{eventId}/recruitment-posts", eventId)
            .header("Authorization", fakeAccessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location",
            String.format("/events/%s/recruitment-posts/%s", eventId, postId)))
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
    mockMvc.perform(delete("/events/{event-id}/recruitment-posts/{recruitment-post-id}",
            eventId,
            recruitmentPostId)
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
    final Member member1 = new Member(1L, 3L, "https://github.image", "아마란스", "amaran-th");
    final Member member2 = new Member(2L, 4L, "https://github.image", "아마아마", "아마아마");
    final LocalDate postedAt = LocalDate.of(2023, 7, 15);
    final List<RecruitmentPostQueryResponse> response = List.of(
        new RecruitmentPostQueryResponse(1L, "함께해요~", postedAt,
            MemberReferenceResponse.from(member1), 21L, "21번 행사"),
        new RecruitmentPostQueryResponse(2L, "같이 가요~", postedAt,
            MemberReferenceResponse.from(member2), 43L, "43번 행사")
    );

    when(postQueryService.findRecruitmentPosts(eventId)).thenReturn(response);

    //when && then
    mockMvc.perform(get(String.format("/events/%s/recruitment-posts", eventId)))
        .andExpect(status().isOk())
        .andDo(document("find-recruitment-posts", RECRUITMENT_POSTS_RESPONSE_FIELDS));
  }

  @Test
  @DisplayName("참여 게시글을 단건조회할 수 있다.")
  void findRecruitmentPost() throws Exception {
    //given
    final Long eventId = 1L;
    final Long postId = 2L;
    final Long memberId = 1L;
    final Member member = new Member(memberId, 3L, "https://github.image", "아마란스", "amaran-th");
    final LocalDate postedAt = LocalDate.of(2023, 7, 15);

    final RecruitmentPostQueryResponse response = new RecruitmentPostQueryResponse(1L, "함께해요~",
        postedAt,
        MemberReferenceResponse.from(member), 21L, "21번 행사");
    when(postQueryService.findRecruitmentPost(eventId, postId)).thenReturn(response);

    //when && then
    mockMvc.perform(get(String.format("/events/%s/recruitment-posts/%s", eventId, postId)))
        .andExpect(status().isOk())
        .andDo(document("find-recruitment-post", RECRUITMENT_POST_RESPONSE_FIELDS));
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
    mockMvc.perform(put("/events/{eventId}/recruitment-posts/{recruitment-post-id}",
            eventId, recruitmentPostId)
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
            get("/events/{eventId}/recruitment-posts/already-recruitment?member-id={memberId}"
                , eventId, memberId)
        )
        .andExpect(status().isOk())
        .andDo(document("check-already-recruitment-post"));
  }

  @Test
  @DisplayName("사용자의 모든 함께가기 요청 목록을 조회한다.")
  void findRecruitmentPostsByMemberIdTest() throws Exception {
    //given
    final Long memberId = 1L;
    final Member member = new Member(memberId, 3L, "https://github.image", "아마란스", "amaran-th");
    final LocalDate postedAt = LocalDate.of(2023, 7, 15);
    final List<RecruitmentPostQueryResponse> response = List.of(
        new RecruitmentPostQueryResponse(1L, "함께해요~", postedAt,
            MemberReferenceResponse.from(member), 21L,"21번 행사"),
        new RecruitmentPostQueryResponse(2L, "같이 가요~", postedAt,
            MemberReferenceResponse.from(member), 43L, "43번 행사")
    );

    //when
    given(postQueryService.findRecruitmentPostsByMemberId(any(), any())).willReturn(response);

    //then
    mockMvc.perform(
            get("/events/recruitment-posts?member-id={memberId}", memberId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer AccessToken"))
        .andExpect(status().isOk())
        .andDo(document("find-all-by-member-id-recruitment-post",
            RECRUITMENT_POSTS_RESPONSE_FIELDS));
  }
}
