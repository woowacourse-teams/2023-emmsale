package com.emmsale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.feed.application.dto.FeedResponseRefactor;
import com.emmsale.feed.application.dto.FeedUpdateRequest;
import com.emmsale.feed.application.dto.FeedUpdateResponse;
import com.emmsale.member.application.dto.MemberReferenceResponse;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

class FeedApiTest extends MockMvcTestHelper {

  private static final MemberReferenceResponse MEMBER_REFERENCE_RESPONSE = new MemberReferenceResponse(
      2L,
      "멤버",
      "멤버 설명",
      "멤버 이미지url",
      "멤버 깃허브 url"
  );
  private static final ResponseFieldsSnippet FEEDS_RESPONSE_FIELDS = responseFields(
      fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("피드 id"),
      fieldWithPath("[].eventId").type(JsonFieldType.NUMBER).description("피드가 작성된 부모 이벤트 id"),
      fieldWithPath("[].title").type(JsonFieldType.STRING).description("피드 제목"),
      fieldWithPath("[].content").type(JsonFieldType.STRING).description("피드 내용"),
      fieldWithPath("[].images").type(JsonFieldType.ARRAY).description("피드 이미지 url 리스트"),
      fieldWithPath("[].writer.id").type(JsonFieldType.NUMBER).description("writer의 식별자"),
      fieldWithPath("[].writer.name").type(JsonFieldType.STRING).description("writer의 이름"),
      fieldWithPath("[].writer.description").type(JsonFieldType.STRING)
          .description("writer의 한줄 자기소개"),
      fieldWithPath("[].writer.imageUrl").type(JsonFieldType.STRING)
          .description("writer의 이미지 url"),
      fieldWithPath("[].writer.githubUrl").type(JsonFieldType.STRING)
          .description("writer의 github Url"),
      fieldWithPath("[].commentCount").type(JsonFieldType.NUMBER).description("피드의 댓글 개수"),
      fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("피드 생성 일시"),
      fieldWithPath("[].updatedAt").type(JsonFieldType.STRING).description("피드 업데이트 일시")
  );

  @Test
  @DisplayName("이벤트의 피드 목록을 성공적으로 반환하면 200 OK를 반환한다.")
  void findAllFeedsTest() throws Exception {
    //given
    final long eventId = 11L;
    final List<FeedResponseRefactor> response = List.of(
        new FeedResponseRefactor(
            34L, eventId, "피드 1 제목", "피드 1 내용",
            MEMBER_REFERENCE_RESPONSE,
            Collections.emptyList(),
            2L,
            LocalDateTime.of(2023, 7, 13, 0, 0), LocalDateTime.of(2023, 7, 13, 0, 0)
        ),
        new FeedResponseRefactor(
            35L, eventId, "피드 2 제목", "피드 2 내용",
            MEMBER_REFERENCE_RESPONSE,
            Collections.emptyList(),
            2L,
            LocalDateTime.of(2023, 7, 13, 0, 0), LocalDateTime.of(2023, 7, 13, 0, 0)
        )
    );

    when(feedQueryService.findAllFeeds(any(), any())).thenReturn(response);

    //when & then
    mockMvc.perform(get("/feeds")
            .param("event-id", String.valueOf(eventId)))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("find-all-feed", FEEDS_RESPONSE_FIELDS));
  }

  @Test
  @DisplayName("피드의 상세를 성공적으로 반환하면 200 OK를 반환한다.")
  void findDetailFeedTest() throws Exception {
    //given
    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("id").type(JsonFieldType.NUMBER).description("피드 id"),
        fieldWithPath("eventId").type(JsonFieldType.NUMBER).description("피드가 작성된 부모 이벤트 id"),
        fieldWithPath("title").type(JsonFieldType.STRING).description("피드 제목"),
        fieldWithPath("content").type(JsonFieldType.STRING).description("피드 내용"),
        fieldWithPath("images").type(JsonFieldType.ARRAY).description("피드 이미지 url 리스트"),
        fieldWithPath("writer.id").type(JsonFieldType.NUMBER).description("writer의 식별자"),
        fieldWithPath("writer.name").type(JsonFieldType.STRING).description("writer의 이름"),
        fieldWithPath("writer.description").type(JsonFieldType.STRING)
            .description("writer의 한줄 자기소개"),
        fieldWithPath("writer.imageUrl").type(JsonFieldType.STRING)
            .description("writer의 이미지 url"),
        fieldWithPath("writer.githubUrl").type(JsonFieldType.STRING)
            .description("writer의 github Url"),
        fieldWithPath("commentCount").type(JsonFieldType.NUMBER).description("피드의 댓글 개수"),
        fieldWithPath("createdAt").type(JsonFieldType.STRING).description("피드 생성 일시"),
        fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("피드 업데이트 일시")
    );
    final long eventId = 11L;
    final long feedId = 34L;

    final FeedResponseRefactor response =
        new FeedResponseRefactor(
            34L, eventId, "피드 1 제목", "피드 1 내용",
            MEMBER_REFERENCE_RESPONSE,
            Collections.emptyList(),
            2L,
            LocalDateTime.of(2023, 7, 13, 0, 0), LocalDateTime.of(2023, 7, 13, 0, 0)
        );

    when(feedQueryService.findFeed(any(), any())).thenReturn(response);

    //when & then
    mockMvc.perform(get("/feeds/{feedId}", feedId))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("find-detail-feed", responseFields));
  }

  @Test
  @DisplayName("자신의 피드 목록을 성공적으로 반환하면 200 OK를 반환한다.")
  void findAllMyFeedsTest() throws Exception {
    //given
    final List<FeedResponseRefactor> responses = List.of(
        new FeedResponseRefactor(
            34L, 2L, "피드 1 제목", "피드 1 내용",
            MEMBER_REFERENCE_RESPONSE,
            Collections.emptyList(),
            2L,
            LocalDateTime.of(2023, 7, 13, 0, 0), LocalDateTime.of(2023, 7, 13, 0, 0)
        ),
        new FeedResponseRefactor(
            35L, 2L, "피드 2 제목", "피드 2 내용",
            MEMBER_REFERENCE_RESPONSE,
            Collections.emptyList(),
            2L,
            LocalDateTime.of(2023, 7, 13, 0, 0), LocalDateTime.of(2023, 7, 13, 0, 0)
        )
    );

    when(feedQueryService.findAllMyFeeds(any())).thenReturn(responses);

    //when & then
    mockMvc.perform(get("/feeds/my")
            .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken"))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("find-all-my-feed", FEEDS_RESPONSE_FIELDS));
  }

  @Test
  @DisplayName("이벤트의 피드를 성공적으로 저장하면 201 CREATED를 반환한다.")
  void postFeedTest() throws Exception {
    //given
    final List<MockMultipartFile> images = List.of(
        new MockMultipartFile(
            "picture",
            "picture.jpg",
            MediaType.TEXT_PLAIN_VALUE,
            "test data".getBytes()
        ),
        new MockMultipartFile(
            "picture",
            "picture.jpg",
            MediaType.TEXT_PLAIN_VALUE,
            "test data".getBytes()
        )
    );

    final long eventId = 1L;
    final long feedId = 3L;
    final String 피드_제목 = "피드 제목";
    final String 피드_내용 = "피드 내용";

    final MockMultipartHttpServletRequestBuilder builder = multipart("/feeds")
        .file("eventId", String.valueOf(eventId).getBytes())
        .file("title", 피드_제목.getBytes())
        .file("content", 피드_내용.getBytes())
        .file("images", images.get(0).getBytes())
        .file("images", images.get(1).getBytes());

    when(feedCommandService.postFeed(any(), any(), any())).thenReturn(feedId);

    mockMvc.perform(builder)
        .andExpect(status().isCreated())
        .andDo(print())
        .andDo(document("post-feed"));
  }

  @Test
  @DisplayName("피드를 성공적으로 업데이트하면 200 OK를 반환한다.")
  void updateFeed() throws Exception {
    //given
    final RequestFieldsSnippet requestFields = requestFields(
        fieldWithPath("eventId").type(JsonFieldType.NUMBER).description("이벤트 id"),
        fieldWithPath("title").type(JsonFieldType.STRING).description("피드 제목"),
        fieldWithPath("content").type(JsonFieldType.STRING).description("피드 내용")
    );
    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("id").type(JsonFieldType.NUMBER).description("피드 id"),
        fieldWithPath("eventId").type(JsonFieldType.NUMBER).description("이벤트 id"),
        fieldWithPath("writerId").type(JsonFieldType.NUMBER).description("작성자 id"),
        fieldWithPath("title").type(JsonFieldType.STRING).description("피드 제목"),
        fieldWithPath("content").type(JsonFieldType.STRING).description("피드 내용")
    );

    final long feedId = 134L;
    final long eventId = 1L;
    final String 피드_제목 = "피드 제목";
    final String 피드_내용 = "피드 내용";
    final FeedUpdateRequest request = new FeedUpdateRequest(eventId, 피드_제목, 피드_내용);
    final FeedUpdateResponse response = new FeedUpdateResponse(feedId, eventId, 41L, 피드_제목, 피드_내용);

    when(feedCommandService.updateFeed(any(), any(), any())).thenReturn(response);

    //when & then
    mockMvc.perform(put("/feeds/{feedId}", feedId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("update-feed", requestFields, responseFields));
  }

  @Test
  @DisplayName("피드를 성공적으로 삭제 204 NO_CONTENT를 반환한다.")
  void deleteFeed() throws Exception {
    //given
    final long feedId = 134L;

    //when & then
    mockMvc.perform(delete("/feeds/{feedId}", feedId))
        .andExpect(status().isNoContent())
        .andDo(print())
        .andDo(document("delete-feed"));
  }
}
