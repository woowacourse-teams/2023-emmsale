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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.domain.EventMode;
import com.emmsale.event.domain.PaymentType;
import com.emmsale.scrap.api.ScrapApi;
import com.emmsale.scrap.application.dto.ScrapRequest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

@WebMvcTest(ScrapApi.class)
class ScrapApiTest extends MockMvcTestHelper {

  @Test
  @DisplayName("스크랩 목록을 성공적으로 조회하면 200 OK를 반환한다.")
  void findAllScraps() throws Exception {
    //given
    final List<EventResponse> expectedScrapResponse = List.of(
        new EventResponse(
            1L,
            "인프콘 2023",
            LocalDateTime.parse("2023-06-03T12:00:00"),
            LocalDateTime.parse("2023-09-03T12:00:00"),
            LocalDateTime.parse("2023-09-01T00:00:00"),
            LocalDateTime.parse("2023-09-02T23:59:59"),
            List.of("백엔드", "프론트엔드", "안드로이드", "IOS", "AI"),
            "https://biz.pusan.ac.kr/dext5editordata/2022/08/20220810_160546511_10103.jpg",
            EventMode.ONLINE.getValue(),
            PaymentType.PAID.getValue()
        ),
        new EventResponse(
            5L,
            "웹 컨퍼런스",
            LocalDateTime.parse("2023-07-03T12:00:00"),
            LocalDateTime.parse("2023-08-03T12:00:00"),
            LocalDateTime.parse("2023-06-23T10:00:00"),
            LocalDateTime.parse("2023-07-03T12:00:00"),
            List.of("백엔드", "프론트엔드"),
            "https://biz.pusan.ac.kr/dext5editordata/2022/08/20220810_160546511_10103.jpg",
            EventMode.ONLINE.getValue(),
            PaymentType.PAID.getValue()),
        new EventResponse(2L,
            "AI 컨퍼런스",
            LocalDateTime.parse("2023-07-22T12:00:00"),
            LocalDateTime.parse("2023-07-30T12:00:00"),
            LocalDateTime.parse("2023-07-01T00:00:00"),
            LocalDateTime.parse("2023-07-21T23:59:59"),
            List.of("AI"),
            "https://biz.pusan.ac.kr/dext5editordata/2022/08/20220810_160546511_10103.jpg",
            EventMode.ONLINE.getValue(),
            PaymentType.PAID.getValue())
    );

    final ResponseFieldsSnippet responseFields = PayloadDocumentation.responseFields(
        PayloadDocumentation.fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("행사 id"),
        PayloadDocumentation.fieldWithPath("[].name").type(JsonFieldType.STRING).description("행사명"),
        PayloadDocumentation.fieldWithPath("[].eventStartDate").type(JsonFieldType.STRING)
            .description("행사 시작일(yyyy:MM:dd:HH:mm:ss)"),
        PayloadDocumentation.fieldWithPath("[].eventEndDate").type(JsonFieldType.STRING)
            .description("행사 마감일(yyyy:MM:dd:HH:mm:ss)"),
        PayloadDocumentation.fieldWithPath("[].applyStartDate").type(JsonFieldType.STRING)
            .description("행사 시작일(yyyy:MM:dd:HH:mm:ss)"),
        PayloadDocumentation.fieldWithPath("[].applyEndDate").type(JsonFieldType.STRING)
            .description("행사 마감일(yyyy:MM:dd:HH:mm:ss)"),
        PayloadDocumentation.fieldWithPath("[].tags[]").type(JsonFieldType.ARRAY)
            .description("행사 태그 목록"),
        PayloadDocumentation.fieldWithPath("[].imageUrl").type(JsonFieldType.STRING)
            .description("행사 이미지 URL"),
        PayloadDocumentation.fieldWithPath("[].eventMode").type(JsonFieldType.STRING)
            .description("행사 온라인 여부(온라인, 오프라인, 온오프라인)"),
        PayloadDocumentation.fieldWithPath("[].paymentType").type(JsonFieldType.STRING)
            .description("행사 유료 여부(유료, 무료, 유무료)")
    );

    //when
    when(scrapQueryService.findAllScraps(any())).thenReturn(expectedScrapResponse);

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
