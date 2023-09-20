package com.emmsale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.feed.application.dto.FeedDetailResponse;
import com.emmsale.feed.application.dto.FeedDetailResponse.WriterProfileResponse;
import com.emmsale.feed.application.dto.FeedListResponse;
import com.emmsale.feed.application.dto.FeedPostRequest;
import com.emmsale.feed.application.dto.FeedPostResponse;
import com.emmsale.feed.application.dto.FeedSimpleResponse;
import com.emmsale.feed.application.dto.FeedUpdateRequest;
import com.emmsale.feed.application.dto.FeedUpdateResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

class FeedApiTest extends MockMvcTestHelper {

  @Test
  @DisplayName("이벤트의 피드 목록을 성공적으로 반환하면 200 OK를 반환한다.")
  void findAllFeedsTest() throws Exception {
    //given
    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("eventId").type(JsonFieldType.NUMBER).description("피드가 작성된 부모 이벤트 id"),
        fieldWithPath("feeds").type(JsonFieldType.ARRAY).description("피드 리스트"),
        fieldWithPath("feeds[].id").type(JsonFieldType.NUMBER).description("피드 id"),
        fieldWithPath("feeds[].title").type(JsonFieldType.STRING).description("피드 제목"),
        fieldWithPath("feeds[].writerId").type(JsonFieldType.NUMBER).description("피드 작성자 id"),
        fieldWithPath("feeds[].commentsCount").type(JsonFieldType.NUMBER).description("피드의 댓글 개수"),
        fieldWithPath("feeds[].createdAt").type(JsonFieldType.STRING).description("피드 작성 일시")
    );

    final long eventId = 11L;
    final List<FeedSimpleResponse> feeds = List.of(
        new FeedSimpleResponse(34L, "피드1 제목", 23L, 0L,
            LocalDateTime.of(LocalDate.of(2023, 7, 13), LocalTime.of(11, 43, 11))),
        new FeedSimpleResponse(35L, "피드2 제목", 43L, 3L,
            LocalDateTime.of(LocalDate.of(2023, 7, 22), LocalTime.of(23, 54, 49)))
    );
    final FeedListResponse response = new FeedListResponse(eventId, feeds);

    when(feedQueryService.findAllFeeds(any())).thenReturn(response);

    //when & then
    mockMvc.perform(get("/feeds")
            .param("event-id", String.valueOf(eventId)))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("find-all-feed", responseFields));
  }

  @Test
  @DisplayName("피드의 상세를 성공적으로 반환하면 200 OK를 반환한다.")
  void findDetailFeedTest() throws Exception {
    //given
    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("id").type(JsonFieldType.NUMBER).description("피드 id"),
        fieldWithPath("eventId").type(JsonFieldType.NUMBER).description("이벤트 id"),
        fieldWithPath("writer").type(JsonFieldType.OBJECT).description("작성자"),
        fieldWithPath("writer.memberId").type(JsonFieldType.NUMBER).description("작성자 id"),
        fieldWithPath("writer.name").type(JsonFieldType.STRING).description("작성자명"),
        fieldWithPath("writer.imageUrl").type(JsonFieldType.STRING).description("작성자 이미지 url"),
        fieldWithPath("title").type(JsonFieldType.STRING).description("피드 제목"),
        fieldWithPath("content").type(JsonFieldType.STRING).description("피드 내용")
    );

    final long eventId = 11L;
    final long feedId = 34L;
    final WriterProfileResponse writer = new WriterProfileResponse(8L, "작성자명",
        "https://member-image.com");
    final FeedDetailResponse response = new FeedDetailResponse(feedId, eventId, writer, "피드 제목",
        "피드 상세 내용");

    when(feedQueryService.findFeed(any())).thenReturn(response);

    //when & then
    mockMvc.perform(get("/feeds/{feedId}", feedId))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("find-detail-feed", responseFields));
  }

  @Test
  @DisplayName("이벤트의 피드를 성공적으로 저장하면 201 CREATED를 반환한다.")
  void postFeedTest() throws Exception {
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

    final long eventId = 1L;
    final String 피드_제목 = "피드 제목";
    final String 피드_내용 = "피드 내용";
    final FeedPostRequest request = new FeedPostRequest(eventId, 피드_제목, 피드_내용);
    final FeedPostResponse response = new FeedPostResponse(134L, eventId, 41L, 피드_제목, 피드_내용);

    when(feedCommandService.postFeed(any(), any())).thenReturn(response);

    //when & then
    mockMvc.perform(post("/feeds")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(print())
        .andDo(document("post-feed", requestFields, responseFields));
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