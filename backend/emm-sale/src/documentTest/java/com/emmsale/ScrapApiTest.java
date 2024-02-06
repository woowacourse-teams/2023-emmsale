package com.emmsale;

import static org.mockito.ArgumentMatchers.any;
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
import com.emmsale.event.domain.EventType;
import com.emmsale.event.domain.PaymentType;
import com.emmsale.scrap.api.ScrapApi;
import com.emmsale.scrap.application.dto.ScrapRequest;
import com.emmsale.tag.application.dto.TagResponse;
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

  private static final List<TagResponse> TAG_RESPONSES = List.of(
      new TagResponse(1L, "백엔드"), new TagResponse(2L, "프론트엔드"),
      new TagResponse(3L, "안드로이드"), new TagResponse(4L, "IOS"),
      new TagResponse(5L, "AI")
  );

  static final ResponseFieldsSnippet SCRAPPED_EVENT_RESPONSE_FIELDS = PayloadDocumentation.responseFields(
      fieldWithPath("id").type(JsonFieldType.NUMBER).description("행사 식별자"),
      fieldWithPath("name").type(JsonFieldType.STRING)
          .description("행사 이름"),
      fieldWithPath("informationUrl").type(JsonFieldType.STRING)
          .description("행사 상세정보 url"),
      fieldWithPath("startDate").type(JsonFieldType.STRING)
          .description("행사 시작 일자"),
      fieldWithPath("endDate").type(JsonFieldType.STRING).description("행사 종료 일자"),
      fieldWithPath("applyStartDate").type(JsonFieldType.STRING)
          .description("행사 신청 시작 일자(nullable)"),
      fieldWithPath("applyEndDate").type(JsonFieldType.STRING)
          .description("행사 신청 종료 일자(nullable)"),
      fieldWithPath("location").type(JsonFieldType.STRING).description("행사 장소"),
      fieldWithPath("tags[]").type(JsonFieldType.ARRAY).description("태그들"),
      fieldWithPath("tags[].id").type(JsonFieldType.NUMBER).description("행사 태그 ID"),
      fieldWithPath("tags[].name").type(JsonFieldType.STRING).description("행사 태그 이름"),
      fieldWithPath("thumbnailUrl").type(JsonFieldType.STRING)
          .description("행사 섬네일 이미지 Url(포스터)"),
      fieldWithPath("type").type(JsonFieldType.STRING)
          .description("행사의 분류"),
      fieldWithPath("imageUrls[]").description("행사의 상세 정보 이미지 URL들").optional(),
      fieldWithPath("organization").description("행사 주최기관"),
      fieldWithPath("paymentType").description("행사의 유무료 여부(유료,무료,유무료)"),
      fieldWithPath("eventMode").description("행사의 온/오프라인 여부(온라인,오프라인,온오프라인)")
  );

  @Test
  @DisplayName("스크랩 목록을 성공적으로 조회하면 200 OK를 반환한다.")
  void findAllScraps() throws Exception {
    //given
    final List<EventResponse> expectedScrapResponse = List.of(
        new EventResponse(
            1L,
            "인프콘 2023",
            "https://aaa",
            LocalDateTime.parse("2023-06-03T12:00:00"),
            LocalDateTime.parse("2023-09-03T12:00:00"),
            LocalDateTime.parse("2023-09-01T00:00:00"),
            LocalDateTime.parse("2023-09-02T23:59:59"),
            "코엑스",
            TAG_RESPONSES,
            "image0.jpg",
            EventType.CONFERENCE.name(),
            List.of("image1.jpg", "image2.jpg", "image3.jpg"),
            "인프런",
            PaymentType.PAID.getValue(),
            EventMode.ONLINE.getValue()
        ),
        new EventResponse(
            5L,
            "웹 컨퍼런스",
            "https://aaa",
            LocalDateTime.parse("2023-06-03T12:00:00"),
            LocalDateTime.parse("2023-09-03T12:00:00"),
            LocalDateTime.parse("2023-09-01T00:00:00"),
            LocalDateTime.parse("2023-09-02T23:59:59"),
            "코엑스",
            TAG_RESPONSES,
            "image0.jpg",
            EventType.CONFERENCE.name(),
            List.of("image1.jpg", "image2.jpg", "image3.jpg"),
            "인프런",
            PaymentType.PAID.getValue(),
            EventMode.ONLINE.getValue()),
        new EventResponse(2L,
            "AI 컨퍼런스",
            "https://aaa",
            LocalDateTime.parse("2023-06-03T12:00:00"),
            LocalDateTime.parse("2023-09-03T12:00:00"),
            LocalDateTime.parse("2023-09-01T00:00:00"),
            LocalDateTime.parse("2023-09-02T23:59:59"),
            "코엑스",
            TAG_RESPONSES,
            "image0.jpg",
            EventType.CONFERENCE.name(),
            List.of("image1.jpg", "image2.jpg", "image3.jpg"),
            "인프런",
            PaymentType.PAID.getValue(),
            EventMode.ONLINE.getValue())
    );

    final ResponseFieldsSnippet responseFields = PayloadDocumentation.responseFields(
        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("행사 식별자"),
        fieldWithPath("[].name").type(JsonFieldType.STRING)
            .description("행사 이름"),
        fieldWithPath("[].informationUrl").type(JsonFieldType.STRING)
            .description("행사 상세정보 url"),
        fieldWithPath("[].startDate").type(JsonFieldType.STRING)
            .description("행사 시작 일자"),
        fieldWithPath("[].endDate").type(JsonFieldType.STRING).description("행사 종료 일자"),
        fieldWithPath("[].applyStartDate").type(JsonFieldType.STRING)
            .description("행사 신청 시작 일자(nullable)"),
        fieldWithPath("[].applyEndDate").type(JsonFieldType.STRING)
            .description("행사 신청 종료 일자(nullable)"),
        fieldWithPath("[].location").type(JsonFieldType.STRING).description("행사 장소"),
        fieldWithPath("[].tags[]").type(JsonFieldType.ARRAY).description("태그들"),
        fieldWithPath("[].tags[].id").type(JsonFieldType.NUMBER).description("행사 태그 ID"),
        fieldWithPath("[].tags[].name").type(JsonFieldType.STRING).description("행사 태그 이름"),
        fieldWithPath("[].thumbnailUrl").type(JsonFieldType.STRING)
            .description("행사 섬네일 이미지 Url(포스터)"),
        fieldWithPath("[].type").type(JsonFieldType.STRING)
            .description("행사의 분류"),
        fieldWithPath("[].imageUrls[]").description("행사의 상세 정보 이미지 URL들").optional(),
        fieldWithPath("[].organization").description("행사 주최기관"),
        fieldWithPath("[].paymentType").description("행사의 유무료 여부(유료,무료,유무료)"),
        fieldWithPath("[].eventMode").description("행사의 온/오프라인 여부(온라인,오프라인,온오프라인)")
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
    final EventResponse expectedScrapResponse = new EventResponse(
        1L,
        "인프콘 2023",
        "https://aaa",
        LocalDateTime.parse("2023-06-03T12:00:00"),
        LocalDateTime.parse("2023-09-03T12:00:00"),
        LocalDateTime.parse("2023-09-01T00:00:00"),
        LocalDateTime.parse("2023-09-02T23:59:59"),
        "코엑스",
        TAG_RESPONSES,
        "image0.jpg",
        EventType.CONFERENCE.name(),
        List.of("image1.jpg", "image2.jpg", "image3.jpg"),
        "인프런",
        PaymentType.PAID.getValue(),
        EventMode.ONLINE.getValue()
    );
    final RequestFieldsSnippet requestFields = requestFields(
        fieldWithPath("eventId").description("스크랩할 이벤트 id")
    );

    //when
    when(scrapCommandService.append(any(), any())).thenReturn(expectedScrapResponse);

    //then
    mockMvc.perform(post("/scraps")
            .header(HttpHeaders.AUTHORIZATION, "Bearer AccessToken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(document("append-scrap", requestFields, SCRAPPED_EVENT_RESPONSE_FIELDS));
  }

  @Test
  @DisplayName("스크랩을 성공적으로 삭제하면 204 NO_CONTENT를 반환한다.")
  void deleteScrap() throws Exception {
    //given
    final long eventId = 1L;
    final EventResponse expectedScrapResponse = new EventResponse(
        1L,
        "인프콘 2023",
        "https://aaa",
        LocalDateTime.parse("2023-06-03T12:00:00"),
        LocalDateTime.parse("2023-09-03T12:00:00"),
        LocalDateTime.parse("2023-09-01T00:00:00"),
        LocalDateTime.parse("2023-09-02T23:59:59"),
        "코엑스",
        TAG_RESPONSES,
        "image0.jpg",
        EventType.CONFERENCE.name(),
        List.of("image1.jpg", "image2.jpg", "image3.jpg"),
        "인프런",
        PaymentType.PAID.getValue(),
        EventMode.ONLINE.getValue()
    );

    //when
    when(scrapCommandService.deleteScrap(any(), any())).thenReturn(expectedScrapResponse);

    //then
    mockMvc.perform(delete("/scraps?event-id={eventId}", eventId)
            .header(HttpHeaders.AUTHORIZATION, "Bearer AccessToken"))
        .andExpect(status().isOk())
        .andDo(document("delete-scrap", SCRAPPED_EVENT_RESPONSE_FIELDS));
  }
}
