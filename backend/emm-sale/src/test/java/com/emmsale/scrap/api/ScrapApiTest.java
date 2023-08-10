package com.emmsale.scrap.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.helper.MockMvcTestHelper;
import com.emmsale.scrap.application.ScrapCommandService;
import com.emmsale.scrap.application.dto.ScrapRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

@WebMvcTest(ScrapApi.class)
class ScrapApiTest extends MockMvcTestHelper {

  @MockBean
  private ScrapCommandService scrapCommandService;

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
    final long scrapId = 1L;

    //when
    doNothing().when(scrapCommandService).deleteScrap(any(), any());

    //then
    mockMvc.perform(delete("/scraps/{scrapId}", scrapId)
            .header(HttpHeaders.AUTHORIZATION, "Bearer AccessToken"))
        .andExpect(status().isNoContent())
        .andDo(document("delete-scrap"));
  }
}
