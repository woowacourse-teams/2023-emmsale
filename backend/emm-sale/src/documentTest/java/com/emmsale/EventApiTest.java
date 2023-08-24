package com.emmsale;

import com.emmsale.event.EventFixture;
import com.emmsale.event.api.EventApi;
import com.emmsale.event.application.dto.EventDetailRequest;
import com.emmsale.event.application.dto.EventDetailResponse;
import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventStatus;
import com.emmsale.event.domain.EventType;
import com.emmsale.tag.TagFixture;
import com.emmsale.tag.application.dto.TagRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.request.RequestParametersSnippet;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(EventApi.class)
class EventApiTest extends MockMvcTestHelper {

  private static final ResponseFieldsSnippet EVENT_DETAIL_RESPONSE_FILED = PayloadDocumentation.responseFields(
      PayloadDocumentation.fieldWithPath("id").type(JsonFieldType.NUMBER).description("event 식별자"),
      PayloadDocumentation.fieldWithPath("name").type(JsonFieldType.STRING)
          .description("envent 이름"),
      PayloadDocumentation.fieldWithPath("informationUrl").type(JsonFieldType.STRING)
          .description("상세정보 url"),
      PayloadDocumentation.fieldWithPath("startDate").type(JsonFieldType.STRING)
          .description("시작일자"),
      PayloadDocumentation.fieldWithPath("endDate").type(JsonFieldType.STRING).description("종료일자"),
      PayloadDocumentation.fieldWithPath("applyStartDate").type(JsonFieldType.STRING)
          .description("신청 시작일자(nullable)"),
      PayloadDocumentation.fieldWithPath("applyEndDate").type(JsonFieldType.STRING)
          .description("신청 종료일자(nullable)"),
      PayloadDocumentation.fieldWithPath("location").type(JsonFieldType.STRING).description("장소"),
      PayloadDocumentation.fieldWithPath("status").type(JsonFieldType.STRING).description("진행상태"),
      PayloadDocumentation.fieldWithPath("applyStatus").type(JsonFieldType.STRING)
          .description("행사 신청 기간의 진행 상황"),
      PayloadDocumentation.fieldWithPath("tags[]").type(JsonFieldType.ARRAY).description("태그들"),
      PayloadDocumentation.fieldWithPath("imageUrl").type(JsonFieldType.STRING)
          .description("이미지 Url(포스터)"),
      PayloadDocumentation.fieldWithPath("remainingDays").type(JsonFieldType.NUMBER)
          .description("시작일로 부터 D-day"),
      PayloadDocumentation.fieldWithPath("applyRemainingDays").type(JsonFieldType.NUMBER)
          .description("행사 신청 시작일까지 남은 일 수"),
      PayloadDocumentation.fieldWithPath("type").type(JsonFieldType.STRING)
          .description("event의 타입"));

  @Test
  @DisplayName("컨퍼런스의 상세정보를 조회할 수 있다.")
  void findEvent() throws Exception {
    //given
    final Long eventId = 1L;
    final EventDetailResponse eventDetailResponse = new EventDetailResponse(eventId, "인프콘 2023",
        "http://infcon.com", LocalDateTime.of(2023, 8, 15, 12, 0),
        LocalDateTime.of(2023, 8, 15, 12, 0), LocalDateTime.of(2023, 8, 1, 12, 0),
        LocalDateTime.of(2023, 8, 15, 12, 0), "코엑스",
        "UPCOMING",
        "ENDED", List.of("코틀린", "백엔드", "안드로이드"),
        "https://www.image.com", 2, -12, EventType.COMPETITION.toString());

    Mockito.when(eventService.findEvent(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
        .thenReturn(eventDetailResponse);

    //when
    mockMvc.perform(MockMvcRequestBuilders.get("/events/" + eventId)).andExpect(
            MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("find-event", EVENT_DETAIL_RESPONSE_FILED));
  }

  @Test
  @DisplayName("특정 카테고리의 행사 목록을 조회할 수 있으면 200 OK를 반환한다.")
  void findEvents() throws Exception {
    // given
    final RequestParametersSnippet requestParameters = RequestDocumentation.requestParameters(
        RequestDocumentation.parameterWithName("category")
            .description("행사 카테고리(CONFERENCE, COMPETITION)"),
        RequestDocumentation.parameterWithName("start_date")
            .description("필터링하려는 기간의 시작일(yyyy:mm:dd)(option)")
            .optional(),
        RequestDocumentation.parameterWithName("end_date")
            .description("필터링하려는 기간의 끝일(yyyy:mm:dd)(option)").optional(),
        RequestDocumentation.parameterWithName("tags").description("필터링하려는 태그(option)").optional(),
        RequestDocumentation.parameterWithName("statuses")
            .description("필터링하려는 상태(UPCOMING, IN_PROGRESS, ENDED)(option)")
            .optional()
    );

    final ResponseFieldsSnippet responseFields = PayloadDocumentation.responseFields(
        PayloadDocumentation.fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("행사 id"),
        PayloadDocumentation.fieldWithPath("[].name").type(JsonFieldType.STRING).description("행사명"),
        PayloadDocumentation.fieldWithPath("[].startDate").type(JsonFieldType.STRING)
            .description("행사 시작일(yyyy:MM:dd:HH:mm:ss)"),
        PayloadDocumentation.fieldWithPath("[].endDate").type(JsonFieldType.STRING)
            .description("행사 마감일(yyyy:MM:dd:HH:mm:ss)"),
        PayloadDocumentation.fieldWithPath("[].tags[]").type(JsonFieldType.ARRAY)
            .description("행사 태그 목록"),
        PayloadDocumentation.fieldWithPath("[].status").type(JsonFieldType.STRING)
            .description("행사 진행 상황(IN_PROGRESS, UPCOMING, ENDED)"),
        PayloadDocumentation.fieldWithPath("[].applyStatus").type(JsonFieldType.STRING)
            .description("행사 신청 기간의 진행 상황(IN_PROGRESS, UPCOMING, ENDED)"),
        PayloadDocumentation.fieldWithPath("[].remainingDays").type(JsonFieldType.NUMBER)
            .description("행사 시작일까지 남은 일 수"),
        PayloadDocumentation.fieldWithPath("[].applyRemainingDays").type(JsonFieldType.NUMBER)
            .description("행사 신청 시작일까지 남은 일 수"),
        PayloadDocumentation.fieldWithPath("[].imageUrl").type(JsonFieldType.STRING)
            .description("행사 이미지 URL")
    );

    final List<EventResponse> eventResponses = List.of(
        new EventResponse(1L, "인프콘 2023", LocalDateTime.parse("2023-06-03T12:00:00"),
            LocalDateTime.parse("2023-09-03T12:00:00"),
            List.of("백엔드", "프론트엔드", "안드로이드", "IOS", "AI"), "IN_PROGRESS", "ENDED",
            "https://biz.pusan.ac.kr/dext5editordata/2022/08/20220810_160546511_10103.jpg",
            3, -30),
        new EventResponse(5L, "웹 컨퍼런스", LocalDateTime.parse("2023-07-03T12:00:00"),
            LocalDateTime.parse("2023-08-03T12:00:00"), List.of("백엔드", "프론트엔드"),
            "IN_PROGRESS", "IN_PROGRESS",
            "https://biz.pusan.ac.kr/dext5editordata/2022/08/20220810_160546511_10103.jpg",
            3, 3),
        new EventResponse(2L, "AI 컨퍼런스", LocalDateTime.parse("2023-07-22T12:00:00"),
            LocalDateTime.parse("2023-07-30T12:00:00"), List.of("AI"), "UPCOMING",
            "IN_PROGRESS",
            "https://biz.pusan.ac.kr/dext5editordata/2022/08/20220810_160546511_10103.jpg",
            3, -18)

    );

    Mockito.when(eventService.findEvents(ArgumentMatchers.any(EventType.class),
        ArgumentMatchers.any(LocalDate.class), ArgumentMatchers.eq("2023-07-01"),
        ArgumentMatchers.eq("2023-07-31"),
        ArgumentMatchers.eq(null), ArgumentMatchers.any())).thenReturn(eventResponses);

    // when & then
    mockMvc.perform(MockMvcRequestBuilders.get("/events")
            .param("category", "CONFERENCE")
            .param("start_date", "2023-07-01")
            .param("end_date", "2023-07-31")
            .param("statuses", "UPCOMING,IN_PROGRESS")
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("find-events", requestParameters, responseFields));
  }

  @Test
  @DisplayName("이벤트를 성공적으로 업데이트하면 200, OK를 반환한다.")
  void updateEventTest() throws Exception {
    //given
    final long eventId = 1L;
    final Event event = EventFixture.인프콘_2023();

    final List<TagRequest> tags = Stream.of(TagFixture.백엔드(), TagFixture.안드로이드())
        .map(tag -> new TagRequest(tag.getName())).collect(Collectors.toList());

    final EventDetailRequest request = new EventDetailRequest(event.getName(), event.getLocation(),
        event.getInformationUrl(), event.getEventPeriod().getStartDate(),
        event.getEventPeriod().getEndDate(),
        event.getEventPeriod().getApplyStartDate(), event.getEventPeriod().getApplyEndDate(), tags,
        event.getImageUrl(), event.getType());

    final EventDetailResponse response = new EventDetailResponse(eventId, request.getName(),
        request.getInformationUrl(), request.getStartDateTime(), request.getEndDateTime(),
        request.getApplyStartDateTime(), request.getApplyEndDateTime(),
        request.getLocation(), EventStatus.IN_PROGRESS.name(), EventStatus.ENDED.name(),
        tags.stream().map(TagRequest::getName).collect(Collectors.toList()), request.getImageUrl(),
        10, 10, request.getType().toString());

    Mockito.when(eventService.updateEvent(ArgumentMatchers.any(), ArgumentMatchers.any(),
        ArgumentMatchers.any())).thenReturn(response);

    final RequestFieldsSnippet requestFields = PayloadDocumentation.requestFields(
        PayloadDocumentation.fieldWithPath("name").type(JsonFieldType.STRING)
            .description("행사(Event) 이름"),
        PayloadDocumentation.fieldWithPath("location").type(JsonFieldType.STRING)
            .description("행사(Event) 장소"),
        PayloadDocumentation.fieldWithPath("startDateTime").type(JsonFieldType.STRING)
            .description("행사(Event) 시작일시"),
        PayloadDocumentation.fieldWithPath("endDateTime").type(JsonFieldType.STRING)
            .description("행사(Event) 종료일시"),
        PayloadDocumentation.fieldWithPath("applyStartDateTime").type(JsonFieldType.STRING)
            .description("행사(Event) 신청시작일시"),
        PayloadDocumentation.fieldWithPath("applyEndDateTime").type(JsonFieldType.STRING)
            .description("행사(Event) 신청종료일시"),
        PayloadDocumentation.fieldWithPath("informationUrl").type(JsonFieldType.STRING)
            .description("행사(Event) 상세 정보 URL"),
        PayloadDocumentation.fieldWithPath("tags[].name").type(JsonFieldType.STRING)
            .description("연관 태그명"),
        PayloadDocumentation.fieldWithPath("imageUrl").type(JsonFieldType.STRING)
            .description("행사(Event) 이미지url"),
        PayloadDocumentation.fieldWithPath("type").type(JsonFieldType.STRING)
            .description("행사(Event) 타입")
    );

    //when
    final ResultActions result = mockMvc.perform(
        MockMvcRequestBuilders.put("/events/" + eventId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)));

    //then
    result.andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print())
        .andDo(MockMvcRestDocumentation.document("update-event", requestFields,
            EVENT_DETAIL_RESPONSE_FILED));
  }

  @Test
  @DisplayName("이벤트를 성공적으로 삭제하면 204, NO_CONTENT를 반환한다.")
  void deleteEventTest() throws Exception {
    //given
    final long eventId = 1L;

    Mockito.doNothing().when(eventService).deleteEvent(eventId);
    //when
    final ResultActions result = mockMvc.perform(
        MockMvcRequestBuilders.delete("/events/" + eventId));

    //then
    result.andExpect(MockMvcResultMatchers.status().isNoContent())
        .andDo(MockMvcResultHandlers.print()).andDo(
            MockMvcRestDocumentation.document("delete-event"));
  }

  @Nested
  class AddEvent {

    @Test
    @DisplayName("이벤트를 성공적으로 추가하면 201, CREATED 를 반환한다.")
    void addEventTest() throws Exception {
      //given
      final Event event = EventFixture.인프콘_2023();

      final List<TagRequest> tags = Stream.of(TagFixture.백엔드(), TagFixture.안드로이드())
          .map(tag -> new TagRequest(tag.getName())).collect(Collectors.toList());

      final EventDetailRequest request = new EventDetailRequest(event.getName(),
          event.getLocation(), event.getInformationUrl(), event.getEventPeriod().getStartDate(),
          event.getEventPeriod().getEndDate(),
          event.getEventPeriod().getApplyStartDate(), event.getEventPeriod().getApplyEndDate(),
          tags, event.getImageUrl(), event.getType());

      final EventDetailResponse response = new EventDetailResponse(1L, request.getName(),
          request.getInformationUrl(), request.getStartDateTime(), request.getEndDateTime(),
          request.getApplyStartDateTime(), request.getApplyEndDateTime(),
          request.getLocation(), EventStatus.IN_PROGRESS.name(), EventStatus.ENDED.name(),
          tags.stream().map(TagRequest::getName).collect(Collectors.toList()),
          request.getImageUrl(), 10, 10, request.getType().toString());

      Mockito.when(eventService.addEvent(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(response);

      final RequestFieldsSnippet requestFields = PayloadDocumentation.requestFields(
          PayloadDocumentation.fieldWithPath("name").type(JsonFieldType.STRING)
              .description("행사(Event) 이름"),
          PayloadDocumentation.fieldWithPath("location").type(JsonFieldType.STRING)
              .description("행사(Event) 장소"),
          PayloadDocumentation.fieldWithPath("startDateTime").type(JsonFieldType.STRING)
              .description("행사(Event) 시작일시"),
          PayloadDocumentation.fieldWithPath("endDateTime").type(JsonFieldType.STRING)
              .description("행사(Event) 종료일시"),
          PayloadDocumentation.fieldWithPath("applyStartDateTime").type(JsonFieldType.STRING)
              .description("행사(Event) 신청시작일시"),
          PayloadDocumentation.fieldWithPath("applyEndDateTime").type(JsonFieldType.STRING)
              .description("행사(Event) 신청종료일시"),
          PayloadDocumentation.fieldWithPath("informationUrl").type(JsonFieldType.STRING)
              .description("행사(Event) 상세 정보 URL"),
          PayloadDocumentation.fieldWithPath("tags[].name").type(JsonFieldType.STRING)
              .description("연관 태그명"),
          PayloadDocumentation.fieldWithPath("imageUrl").type(JsonFieldType.STRING)
              .description("행사(Event) imageUrl"),
          PayloadDocumentation.fieldWithPath("type").type(JsonFieldType.STRING)
              .description("Event 타입"));

      //when
      final ResultActions result = mockMvc.perform(
          MockMvcRequestBuilders.post("/events").contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(objectMapper.writeValueAsString(request)));

      //then
      result.andExpect(MockMvcResultMatchers.status().isCreated())
          .andDo(MockMvcResultHandlers.print())
          .andDo(MockMvcRestDocumentation.document("add-event", requestFields,
              EVENT_DETAIL_RESPONSE_FILED));
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @DisplayName("이름에 빈 값이 들어올 경우 400 BAD_REQUEST를 반환한다.")
    void addEventWithEmptyNameTest(final String eventName) throws Exception {
      //given
      final Event event = EventFixture.인프콘_2023();

      final List<TagRequest> tags = Stream.of(TagFixture.백엔드(), TagFixture.안드로이드())
          .map(tag -> new TagRequest(tag.getName())).collect(Collectors.toList());

      final EventDetailRequest request = new EventDetailRequest(eventName, event.getLocation(),
          event.getInformationUrl(), event.getEventPeriod().getStartDate(),
          event.getEventPeriod().getEndDate(),
          event.getEventPeriod().getApplyStartDate(), event.getEventPeriod().getApplyEndDate(),
          tags, null,
          EventType.COMPETITION);

      //when
      final ResultActions result = mockMvc.perform(
          MockMvcRequestBuilders.post("/events").contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(objectMapper.writeValueAsString(request)));

      //then
      result.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @DisplayName("장소에 빈 값이 들어올 경우 400 BAD_REQUEST를 반환한다.")
    void addEventWithEmptyLocationTest(final String eventLocation) throws Exception {
      //given
      final Event event = EventFixture.인프콘_2023();

      final List<TagRequest> tags = Stream.of(TagFixture.백엔드(), TagFixture.안드로이드())
          .map(tag -> new TagRequest(tag.getName())).collect(Collectors.toList());

      final EventDetailRequest request = new EventDetailRequest(event.getName(), eventLocation,
          event.getInformationUrl(), event.getEventPeriod().getStartDate(),
          event.getEventPeriod().getEndDate(),
          event.getEventPeriod().getApplyStartDate(), event.getEventPeriod().getApplyEndDate(),
          tags,
          event.getImageUrl(), event.getType());

      //when
      final ResultActions result = mockMvc.perform(
          MockMvcRequestBuilders.post("/events").contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(objectMapper.writeValueAsString(request)));

      //then
      result.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"httpexample.com", "http:example.com", "http:/example.com",
        "httpsexample.com", "https:example.com", "https:/example.com"})
    @NullSource
    @DisplayName("상세 URL에 http:// 혹은 https://로 시작하지 않는 값이 들어올 경우 400 BAD_REQUEST를 반환한다.")
    void addEventWithInvalidInformationUrlTest(final String informationUrl) throws Exception {
      //given
      final Event event = EventFixture.인프콘_2023();

      final List<TagRequest> tags = Stream.of(TagFixture.백엔드(), TagFixture.안드로이드())
          .map(tag -> new TagRequest(tag.getName())).collect(Collectors.toList());

      final EventDetailRequest request = new EventDetailRequest(event.getName(),
          event.getLocation(), informationUrl, event.getEventPeriod().getStartDate(),
          event.getEventPeriod().getEndDate(),
          event.getEventPeriod().getApplyStartDate(), event.getEventPeriod().getApplyEndDate(),
          tags,
          event.getImageUrl(), event.getType());

      //when
      final ResultActions result = mockMvc.perform(
          MockMvcRequestBuilders.post("/events").contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(objectMapper.writeValueAsString(request)));

      //then
      result.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"23-01-01T12:00:00", "2023-1-01T12:00:00", "2023-01-1T12:00:00",
        "2023-01-01T2:00:00", "2023-01-01T12:0:00", "2023-01-01T12:00:0"})
    @NullSource
    @DisplayName("시작 일시에 null 혹은 다른 형식의 일시 값이 들어올 경우 400 BAD_REQUEST를 반환한다.")
    void addEventWithUnformattedStartDateTimeTest(final String startDateTime) throws Exception {
      //given
      final Event event = EventFixture.인프콘_2023();

      final List<TagRequest> tags = Stream.of(TagFixture.백엔드(), TagFixture.안드로이드())
          .map(tag -> new TagRequest(tag.getName())).collect(Collectors.toList());

      final String request = "{" + "\"name\":\"인프콘 2023\"," + "\"location\":\"코엑스\","
          + "\"informationUrl\":\"https://~~~\"," + "\"startDateTime\":" + startDateTime + ","
          + "\"endDateTime\":\"2023-01-02T12:00:00\","
          + ",\"tags\":[{\"name\":\"백엔드\"},{\"name\":\"안드로이드\"}]" + "}";

      //when
      final ResultActions result = mockMvc.perform(
          MockMvcRequestBuilders.post("/events").contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(request));

      //then
      result.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"23-01-02T12:00:00", "2023-1-02T12:00:00", "2023-01-2T12:00:00",
        "2023-01-02T2:00:00", "2023-01-02T12:0:00", "2023-01-02T12:00:0"})
    @NullSource
    @DisplayName("종료 일시에 null 혹은 다른 형식의 일시 값이 들어올 경우 400 BAD_REQUEST를 반환한다.")
    void addEventWithUnformattedEndDateTimeTest(final String endDateTime) throws Exception {
      //given
      final Event event = EventFixture.인프콘_2023();

      final List<TagRequest> tags = Stream.of(TagFixture.백엔드(), TagFixture.안드로이드())
          .map(tag -> new TagRequest(tag.getName())).collect(Collectors.toList());

      final String request = "{" + "\"name\":\"인프콘 2023\"," + "\"location\":\"코엑스\","
          + "\"informationUrl\":\"https://~~~\"," + "\"startDateTime\":\"2023-01-01T12:00:00\","
          + "\"endDateTime\":" + endDateTime + ","
          + ",\"tags\":[{\"name\":\"백엔드\"},{\"name\":\"안드로이드\"}]" + "}";

      //when
      final ResultActions result = mockMvc.perform(
          MockMvcRequestBuilders.post("/events")
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(request)
      );

      //then
      result.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
  }

}
