package com.emmsale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.scrap.api.ScrapApi;
import com.emmsale.scrap.application.dto.ScrapRequest;
import com.emmsale.scrap.application.dto.ScrapResponse;
import com.emmsale.tag.TagFixture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

@WebMvcTest(ScrapApi.class)
class ScrapApiTest extends MockMvcTestHelper {

  @Test
  @DisplayName("스크랩 목록을 성공적으로 조회하면 200 OK를 반환한다.")
  void findAllScraps() throws Exception {
    //given
    final List<ScrapResponse> responses = List.of(
        new ScrapResponse(
            1L,
            593L,
            "인프콘 2023",
            "진행 예정",
            "https://infcon-image.com",
            List.of(TagFixture.백엔드().getName(), TagFixture.안드로이드().getName(),
                TagFixture.프론트엔드().getName(), TagFixture.IOS().getName())
        ),
        new ScrapResponse(
            2L,
            842L,
            "구름톤",
            "종료된 행사",
            "https://goormthon-image.com",
            List.of(TagFixture.백엔드().getName(), TagFixture.프론트엔드().getName())
        )
    );

    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("[].scrapId").type(JsonFieldType.NUMBER).description("스크랩 id"),
        fieldWithPath("[].eventId").type(JsonFieldType.NUMBER).description("행사 id"),
        fieldWithPath("[].name").type(JsonFieldType.STRING).description("행사명"),
        fieldWithPath("[].status").type(JsonFieldType.STRING).description("행사 상태"),
        fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("행사 이미지 url"),
        fieldWithPath("[].tags[]").type(JsonFieldType.ARRAY).description("행사 태그 목록")
    );

    //when
    when(scrapQueryService.findAllScraps(any())).thenReturn(responses);

    //then
    mockMvc.perform(get("/scraps")
            .header(HttpHeaders.AUTHORIZATION, "Bearer AccessToken"))
        .andExpect(status().isOk())
        .andDo(document("find-all-scraps", responseFields));
  }

  @Test
  @DisplayName("스크랩을 성공적으로 추가하면 201 CREATED를 반환한다.")
  void append() throws Exception {
    //given
    final long eventId = 1L;

    final ScrapRequest request = new ScrapRequest(eventId);

    final RequestFieldsSnippet requestFields = requestFields(
        fieldWithPath("eventId").description("스크랩할 이벤트 id")
    );

    //when
    doNothing().when(scrapCommandService).append(any(), any());

    //then
    mockMvc.perform(post("/scraps")
            .header(HttpHeaders.AUTHORIZATION, "Bearer AccessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(document("append-scrap", requestFields));
  }

  @Test
  @DisplayName("스크랩을 성공적으로 삭제하면 204 NO_CONTENT를 반환한다.")
  void deleteScrap() throws Exception {
    //given
    final long eventId = 1L;

    //when
    doNothing().when(scrapCommandService).deleteScrap(any(), any());

    //then
    mockMvc.perform(delete("/scraps?event-id={eventId}", eventId)
            .header(HttpHeaders.AUTHORIZATION, "Bearer AccessToken"))
        .andExpect(status().isNoContent())
        .andDo(document("delete-scrap"));
  }
}
