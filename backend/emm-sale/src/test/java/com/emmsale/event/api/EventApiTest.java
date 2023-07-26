package com.emmsale.event.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.event.application.EventService;
import com.emmsale.event.application.dto.EventDetailResponse;
import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.helper.MockMvcTestHelper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.RequestParametersSnippet;

@WebMvcTest(EventApi.class)
class EventApiTest extends MockMvcTestHelper {

  private static final int QUERY_YEAR = 2023;
  private static final int QUERY_MONTH = 7;

  @MockBean
  private EventService eventService;

  @Test
  @DisplayName("컨퍼런스의 상세정보를 조회할 수 있다.")
  void findEvent() throws Exception {
    //given
    final Long eventId = 1L;
    final EventDetailResponse eventDetailResponse = new EventDetailResponse(
        eventId,
        "인프콘 2023",
        "http://infcon.com",
        LocalDateTime.of(2023, 8, 15, 12, 0),
        LocalDateTime.of(2023, 8, 15, 12, 0),
        "코엑스",
        "예정"
    );

    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("id").type(JsonFieldType.NUMBER).description("event 식별자"),
        fieldWithPath("name").type(JsonFieldType.STRING).description("envent 이름"),
        fieldWithPath("informationUrl").type(JsonFieldType.STRING).description("상세정보 url"),
        fieldWithPath("startDate").type(JsonFieldType.STRING).description("시작일자"),
        fieldWithPath("endDate").type(JsonFieldType.STRING).description("종료일자"),
        fieldWithPath("location").type(JsonFieldType.STRING).description("장소"),
        fieldWithPath("status").type(JsonFieldType.STRING).description("진행상태")
    );

    when(eventService.findEvent(eventId)).thenReturn(eventDetailResponse);

    //when
    mockMvc.perform(get("/events/" + eventId))
        .andExpect(status().isOk())
        .andDo(document("find-event", responseFields));
  }

  @Test
  @DisplayName("특정 연도&월의 행사 목록을 조회할 수 있으면 200 OK를 반환한다.")
  void findEvents() throws Exception {
    // given
    final RequestParametersSnippet requestParameters = requestParameters(
        parameterWithName("year").description("조회하고자 하는 연도"),
        parameterWithName("month").description("조회하고자 하는 월"),
        parameterWithName("tag").description("필터링하려는 태그").optional(),
        parameterWithName("status").description("필터링하려는 상태").optional()
    );

    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("행사 id"),
        fieldWithPath("[].name").type(JsonFieldType.STRING).description("행사명"),
        fieldWithPath("[].startDate").type(JsonFieldType.STRING).description("행사 시작일"),
        fieldWithPath("[].endDate").type(JsonFieldType.STRING).description("행사 종료일"),
        fieldWithPath("[].tags[]").type(JsonFieldType.ARRAY)
            .description("행사 태그 목록"),
        fieldWithPath("[].status").type(JsonFieldType.STRING).description("행사 진행 상황")
    );

    final List<EventResponse> eventResponses = List.of(
        new EventResponse(1L, "인프콘 2023", LocalDateTime.parse("2023-06-03T12:00:00"),
            LocalDateTime.parse("2023-09-03T12:00:00"),
            List.of("백엔드", "프론트엔드", "안드로이드", "IOS", "AI"), "진행 중"),
        new EventResponse(5L, "웹 컨퍼런스", LocalDateTime.parse("2023-07-03T12:00:00"),
            LocalDateTime.parse("2023-08-03T12:00:00"), List.of("백엔드", "프론트엔드"), "진행 중"),
        new EventResponse(2L, "AI 컨퍼런스", LocalDateTime.parse("2023-07-22T12:00:00"),
            LocalDateTime.parse("2023-07-30T12:00:00"), List.of("AI"), "진행 예정"),
        new EventResponse(4L, "안드로이드 컨퍼런스", LocalDateTime.parse("2023-06-29T12:00:00"),
            LocalDateTime.parse("2023-07-16T12:00:00"), List.of("백엔드", "프론트엔드"), "종료된 행사")

    );

    when(eventService.findEvents(any(LocalDate.class), eq(QUERY_YEAR), eq(QUERY_MONTH), eq(null),
        eq(null))).thenReturn(eventResponses);

    // when & then
    mockMvc.perform(get("/events")
            .param("year", "2023")
            .param("month", "7")
        )
        .andExpect(status().isOk())
        .andDo(document("find-events", requestParameters, responseFields));
  }

}