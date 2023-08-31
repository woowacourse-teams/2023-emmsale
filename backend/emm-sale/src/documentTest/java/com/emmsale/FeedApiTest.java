package com.emmsale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.feed.application.dto.FeedPostRequest;
import com.emmsale.feed.application.dto.FeedPostResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

class FeedApiTest extends MockMvcTestHelper {

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
}
