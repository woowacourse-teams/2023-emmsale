package com.emmsale;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.event.EventFixture;
import com.emmsale.event.api.EventApi;
import com.emmsale.event.application.dto.EventDetailRequest;
import com.emmsale.event.application.dto.EventDetailResponse;
import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventMode;
import com.emmsale.event.domain.EventStatus;
import com.emmsale.event.domain.EventType;
import com.emmsale.event.domain.PaymentType;
import com.emmsale.tag.TagFixture;
import com.emmsale.tag.application.dto.TagRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.request.RequestParametersSnippet;
import org.springframework.restdocs.request.RequestPartsSnippet;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@WebMvcTest(EventApi.class)
class EventApiTest extends MockMvcTestHelper {

  private static final ResponseFieldsSnippet EVENT_DETAIL_RESPONSE_FILED = PayloadDocumentation.responseFields(
      fieldWithPath("id").type(JsonFieldType.NUMBER).description("event 식별자"),
      fieldWithPath("name").type(JsonFieldType.STRING)
          .description("envent 이름"),
      fieldWithPath("informationUrl").type(JsonFieldType.STRING)
          .description("상세정보 url"),
      fieldWithPath("startDate").type(JsonFieldType.STRING)
          .description("시작일자"),
      fieldWithPath("endDate").type(JsonFieldType.STRING).description("종료일자"),
      fieldWithPath("applyStartDate").type(JsonFieldType.STRING)
          .description("신청 시작일자(nullable)"),
      fieldWithPath("applyEndDate").type(JsonFieldType.STRING)
          .description("신청 종료일자(nullable)"),
      fieldWithPath("location").type(JsonFieldType.STRING).description("장소"),
      fieldWithPath("status").type(JsonFieldType.STRING).description("진행상태"),
      fieldWithPath("applyStatus").type(JsonFieldType.STRING)
          .description("행사 신청 기간의 진행 상황"),
      fieldWithPath("tags[]").type(JsonFieldType.ARRAY).description("태그들"),
      fieldWithPath("imageUrl").type(JsonFieldType.STRING)
          .description("이미지 Url(포스터)"),
      fieldWithPath("remainingDays").type(JsonFieldType.NUMBER)
          .description("시작일로 부터 D-day"),
      fieldWithPath("applyRemainingDays").type(JsonFieldType.NUMBER)
          .description("행사 신청 시작일까지 남은 일 수"),
      fieldWithPath("type").type(JsonFieldType.STRING)
          .description("event의 타입"),
      fieldWithPath("imageUrls[]").description("이미지 URL들").optional(),
      fieldWithPath("organization").description("행사기관")
  );

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
        "https://www.image.com", 2, -12, EventType.COMPETITION.toString(),
        List.of("imageUrl1", "imageUrl2"), "인프런");

    Mockito.when(eventService.findEvent(ArgumentMatchers.anyLong(), any()))
        .thenReturn(eventDetailResponse);

    //when
    mockMvc.perform(get("/events/" + eventId)).andExpect(
            status().isOk())
        .andDo(MockMvcRestDocumentation.document("find-event", EVENT_DETAIL_RESPONSE_FILED));
  }

  @Test
  @DisplayName("특정 카테고리의 행사 목록을 조회할 수 있으면 200 OK를 반환한다.")
  void findEvents() throws Exception {
    // given
    final RequestParametersSnippet requestParameters = requestParameters(
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
        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("행사 id"),
        fieldWithPath("[].name").type(JsonFieldType.STRING).description("행사명"),
        fieldWithPath("[].startDate").type(JsonFieldType.STRING)
            .description("행사 시작일(yyyy:MM:dd:HH:mm:ss)"),
        fieldWithPath("[].endDate").type(JsonFieldType.STRING)
            .description("행사 마감일(yyyy:MM:dd:HH:mm:ss)"),
        fieldWithPath("[].tags[]").type(JsonFieldType.ARRAY)
            .description("행사 태그 목록"),
        fieldWithPath("[].status").type(JsonFieldType.STRING)
            .description("행사 진행 상황(IN_PROGRESS, UPCOMING, ENDED)"),
        fieldWithPath("[].applyStatus").type(JsonFieldType.STRING)
            .description("행사 신청 기간의 진행 상황(IN_PROGRESS, UPCOMING, ENDED)"),
        fieldWithPath("[].remainingDays").type(JsonFieldType.NUMBER)
            .description("행사 시작일까지 남은 일 수"),
        fieldWithPath("[].applyRemainingDays").type(JsonFieldType.NUMBER)
            .description("행사 신청 시작일까지 남은 일 수"),
        fieldWithPath("[].imageUrl").type(JsonFieldType.STRING)
            .description("행사 이미지 URL"),
        fieldWithPath("[].eventMode").type(JsonFieldType.STRING)
            .description("행사 온라인 여부(온라인, 오프라인, 온오프라인)"),
        fieldWithPath("[].paymentType").type(JsonFieldType.STRING)
            .description("행사 유료 여부(유료, 무료, 유무료)")
    );

    final List<EventResponse> eventResponses = List.of(
        new EventResponse(1L, "인프콘 2023", LocalDateTime.parse("2023-06-03T12:00:00"),
            LocalDateTime.parse("2023-09-03T12:00:00"),
            List.of("백엔드", "프론트엔드", "안드로이드", "IOS", "AI"), "IN_PROGRESS", "ENDED",
            "https://biz.pusan.ac.kr/dext5editordata/2022/08/20220810_160546511_10103.jpg",
            3, -30, EventMode.ONLINE.getValue(), PaymentType.PAID.getValue()),
        new EventResponse(5L, "웹 컨퍼런스", LocalDateTime.parse("2023-07-03T12:00:00"),
            LocalDateTime.parse("2023-08-03T12:00:00"), List.of("백엔드", "프론트엔드"),
            "IN_PROGRESS", "IN_PROGRESS",
            "https://biz.pusan.ac.kr/dext5editordata/2022/08/20220810_160546511_10103.jpg",
            3, 3, EventMode.ONLINE.getValue(), PaymentType.PAID.getValue()),
        new EventResponse(2L, "AI 컨퍼런스", LocalDateTime.parse("2023-07-22T12:00:00"),
            LocalDateTime.parse("2023-07-30T12:00:00"), List.of("AI"), "UPCOMING",
            "IN_PROGRESS",
            "https://biz.pusan.ac.kr/dext5editordata/2022/08/20220810_160546511_10103.jpg",
            3, -18, EventMode.ONLINE.getValue(), PaymentType.PAID.getValue())
    );

    Mockito.when(eventService.findEvents(any(EventType.class),
        any(LocalDate.class), ArgumentMatchers.eq("2023-07-01"),
        ArgumentMatchers.eq("2023-07-31"),
        ArgumentMatchers.eq(null), any())).thenReturn(eventResponses);

    // when & then
    mockMvc.perform(get("/events")
            .param("category", "CONFERENCE")
            .param("start_date", "2023-07-01")
            .param("end_date", "2023-07-31")
            .param("statuses", "UPCOMING,IN_PROGRESS")
        )
        .andExpect(status().isOk())
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
        event.getImageUrl(), event.getType(), EventMode.OFFLINE, PaymentType.PAID, null, "행사기관");

    final EventDetailResponse response = new EventDetailResponse(eventId, request.getName(),
        request.getInformationUrl(), request.getStartDateTime(), request.getEndDateTime(),
        request.getApplyStartDateTime(), request.getApplyEndDateTime(),
        request.getLocation(), EventStatus.IN_PROGRESS.name(), EventStatus.ENDED.name(),
        tags.stream().map(TagRequest::getName).collect(Collectors.toList()), request.getImageUrl(),
        10, 10, request.getType().toString(), Collections.emptyList(), "행사기관");

    Mockito.when(eventService.updateEvent(any(), any(),
        any())).thenReturn(response);

    final RequestFieldsSnippet requestFieldsSnippet = requestFields(
        fieldWithPath("name").description("행사(Event) 이름"),
        fieldWithPath("location").description("행사(Event) 장소"),
        fieldWithPath("startDateTime").description("행사(Event) 시작일시"),
        fieldWithPath("endDateTime").description("행사(Event) 종료일시"),
        fieldWithPath("applyStartDateTime").description("행사(Event) 신청시작일시"),
        fieldWithPath("applyEndDateTime").description("행사(Event) 신청종료일시"),
        fieldWithPath("informationUrl").description("행사(Event) 상세 정보 URL"),
        fieldWithPath("tags[].name").description("연관 태그명"),
        fieldWithPath("imageUrl").description("행사(Event) 이미지url"),
        fieldWithPath("type").description("행사(Event) 타입"),
        fieldWithPath("eventMode").description("행사 온오프라인 여부(ON_OFFLINE, OFFLINE, ONLINE)"),
        fieldWithPath("paymentType").description("행사 유료 여부(PAID, FREE, FREE_PAID)"),
        fieldWithPath("images").description("이미지들").optional(),
        fieldWithPath("organization").description("행사 기관")
    );

    //when & then
    mockMvc.perform(put("/events/" + eventId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(MockMvcResultHandlers.print())
        .andDo(MockMvcRestDocumentation.document("update-event", requestFieldsSnippet,
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
        delete("/events/" + eventId));

    //then
    result.andExpect(status().isNoContent())
        .andDo(MockMvcResultHandlers.print()).andDo(
            MockMvcRestDocumentation.document("delete-event"));
  }

  @Nested
  class AddEvent {

    @Test
    @DisplayName("이벤트를 성공적으로 추가하면 201, CREATED 를 반환한다.")
    void addEventTest() throws Exception {
      //given
      final MockMultipartFile image1 = new MockMultipartFile(
          "picture",
          "picture.jpg",
          MediaType.TEXT_PLAIN_VALUE,
          "test data".getBytes()
      );

      final MockMultipartFile image2 = new MockMultipartFile(
          "picture",
          "picture.jpg",
          MediaType.TEXT_PLAIN_VALUE,
          "test data".getBytes()
      );

      final Event event = EventFixture.인프콘_2023();

      final List<TagRequest> tags = Stream.of(TagFixture.백엔드(), TagFixture.안드로이드())
          .map(tag -> new TagRequest(tag.getName())).collect(Collectors.toList());

      final EventDetailRequest request = new EventDetailRequest(event.getName(),
          event.getLocation(), event.getInformationUrl(), event.getEventPeriod().getStartDate(),
          event.getEventPeriod().getEndDate(),
          event.getEventPeriod().getApplyStartDate(), event.getEventPeriod().getApplyEndDate(),
          tags, event.getImageUrl(), event.getType(), EventMode.ON_OFFLINE, PaymentType.FREE,
          List.of(image1, image2), "행사기관");

      final EventDetailResponse response = new EventDetailResponse(1L, request.getName(),
          request.getInformationUrl(), request.getStartDateTime(), request.getEndDateTime(),
          request.getApplyStartDateTime(), request.getApplyEndDateTime(),
          request.getLocation(), EventStatus.IN_PROGRESS.name(), EventStatus.ENDED.name(),
          tags.stream().map(TagRequest::getName).collect(Collectors.toList()),
          request.getImageUrl(), 10, 10, request.getType().toString(),
          List.of("imageUrl1", "imageUrl2"), "행사기관");

      Mockito.when(eventService.addEvent(any(), any()))
          .thenReturn(response);

      final RequestParametersSnippet requestParam = requestParameters(
          parameterWithName("name").description("행사(Event) 이름"),
          parameterWithName("location").description("행사(Event) 장소"),
          parameterWithName("startDateTime").description("행사(Event) 시작일시"),
          parameterWithName("endDateTime").description("행사(Event) 종료일시"),
          parameterWithName("applyStartDateTime").description("행사(Event) 신청시작일시"),
          parameterWithName("applyEndDateTime").description("행사(Event) 신청종료일시"),
          parameterWithName("informationUrl").description("행사(Event) 상세 정보 URL"),
          parameterWithName("tags").description("연관 태그명"),
          parameterWithName("imageUrl").description("행사(Event) imageUrl"),
          parameterWithName("type").description("Event 타입"),
          parameterWithName("eventMode").description("행사 온오프라인 여부(ON_OFFLINE, OFFLINE, ONLINE)"),
          parameterWithName("paymentType").description("행사 유료 여부(PAID, FREE, FREE_PAID)")
      );

      final RequestPartsSnippet requestPartsSnippet = requestParts(
          partWithName("images").description("이미지들").optional()
      );

      //when
      MockMultipartHttpServletRequestBuilder builder = multipart("/events")
          .file("images", image1.getBytes())
          .file("images", image2.getBytes());

      builder.param("name", request.getName())
          .param("location", request.getLocation())
          .param("informationUrl", request.getInformationUrl())
          .param("startDateTime",
              request.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")))
          .param("endDateTime",
              request.getEndDateTime().format(DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")))
          .param("applyStartDateTime", request.getApplyStartDateTime()
              .format(DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")))
          .param("applyEndDateTime", request.getApplyEndDateTime()
              .format(DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")))
          .param("tags", request.getTags().stream().map(TagRequest::getName)
              .collect(Collectors.joining(",")))
          .param("imageUrl", request.getImageUrl())
          .param("type", request.getType().toString())
          .param("eventMode", request.getEventMode().toString())
          .param("paymentType", request.getPaymentType().toString());

      final ResultActions result = mockMvc.perform(builder);

      //then
      result.andExpect(status().isCreated())
          .andDo(MockMvcResultHandlers.print())
          .andDo(MockMvcRestDocumentation.document("add-event", requestParam,
              EVENT_DETAIL_RESPONSE_FILED, requestPartsSnippet));
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

      //when & then
      mockMvc.perform(post("/events")
              .param("name", eventName)
              .param("location", event.getLocation())
              .param("informationUrl", event.getInformationUrl())
              .param("startDateTime", event.getEventPeriod().getStartDate()
                  .format(DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")))
              .param("endDateTime", event.getEventPeriod().getEndDate()
                  .format(DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")))
              .param("applyStartDateTime", event.getEventPeriod().getApplyStartDate()
                  .format(DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")))
              .param("applyEndDateTime", event.getEventPeriod().getApplyEndDate()
                  .format(DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")))
              .param("imageUrl", event.getImageUrl())
              .param("type", event.getType().toString())
              .param("eventMode", EventMode.ON_OFFLINE.toString())
              .param("paymentType", PaymentType.FREE.toString())
              .param("tags", tags.stream().map(TagRequest::getName)
                  .collect(Collectors.joining(","))))
          .andExpect(status().isBadRequest());
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

      //when & then
      mockMvc.perform(post("/events")
              .param("name", event.getName())
              .param("location", eventLocation)
              .param("informationUrl", eventLocation)
              .param("startDateTime", event.getEventPeriod().getStartDate()
                  .format(DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")))
              .param("endDateTime", event.getEventPeriod().getEndDate()
                  .format(DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")))
              .param("applyStartDateTime", event.getEventPeriod().getApplyStartDate()
                  .format(DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")))
              .param("applyEndDateTime", event.getEventPeriod().getApplyEndDate()
                  .format(DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")))
              .param("imageUrl", event.getImageUrl())
              .param("type", event.getType().toString())
              .param("eventMode", EventMode.ON_OFFLINE.toString())
              .param("paymentType", PaymentType.FREE.toString())
              .param("tags", tags.stream().map(TagRequest::getName)
                  .collect(Collectors.joining(","))))
          .andExpect(status().isBadRequest());
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

      //when
      mockMvc.perform(post("/events")
              .param("name", event.getName())
              .param("location", event.getLocation())
              .param("informationUrl", informationUrl)
              .param("startDateTime", event.getEventPeriod().getStartDate()
                  .format(DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")))
              .param("endDateTime", event.getEventPeriod().getEndDate()
                  .format(DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")))
              .param("applyStartDateTime", event.getEventPeriod().getApplyStartDate()
                  .format(DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")))
              .param("applyEndDateTime", event.getEventPeriod().getApplyEndDate()
                  .format(DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss")))
              .param("imageUrl", event.getImageUrl())
              .param("type", event.getType().toString())
              .param("eventMode", EventMode.ON_OFFLINE.toString())
              .param("paymentType", PaymentType.FREE.toString())
              .param("tags", tags.stream().map(TagRequest::getName)
                  .collect(Collectors.joining(","))))
          .andExpect(status().isBadRequest());

    }

    @ParameterizedTest
    @ValueSource(strings = {"23-01-01T12:00:00", "2023-1-01T12:00:00", "2023-01-1T12:00:00",
        "2023-01-01T2:00:00", "2023-01-01T12:0:00", "2023-01-01T12:00:0"})
    @NullSource
    @DisplayName("시작 일시에 null 혹은 다른 형식의 일시 값이 들어올 경우 400 BAD_REQUEST를 반환한다.")
    void addEventWithUnformattedStartDateTimeTest(final String startDateTime) throws Exception {
      //when & then
      mockMvc.perform(post("/events").contentType(MediaType.APPLICATION_JSON_VALUE)
              .param("name", "인프콘 2023")
              .param("location", "코엑스")
              .param("informationUrl", "https://~~~")
              .param("startDateTime", startDateTime) // 이 변수의 값을 확인하고 올바른 값을 설정하세요.
              .param("endDateTime", "2023-01-02T12:00:00")
              .param("tags[0].name", "백엔드")
              .param("tags[1].name", "안드로이드"))
          .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"23-01-02T12:00:00", "2023-1-02T12:00:00", "2023-01-2T12:00:00",
        "2023-01-02T2:00:00", "2023-01-02T12:0:00", "2023-01-02T12:00:0"})
    @NullSource
    @DisplayName("종료 일시에 null 혹은 다른 형식의 일시 값이 들어올 경우 400 BAD_REQUEST를 반환한다.")
    void addEventWithUnformattedEndDateTimeTest(final String endDateTime) throws Exception {
      //when & then
      mockMvc.perform(post("/events")
              .param("name", "인프콘 2023")
              .param("location", "코엑스")
              .param("informationUrl", "https://~~~")
              .param("startDateTime", "2023-01-01T12:00:00")
              .param("endDateTime", endDateTime) // 이 변수의 값을 확인하고 올바른 값을 설정하세요.
              .param("tags[0].name", "백엔드")
              .param("tags[1].name", "안드로이드"))
          .andExpect(status().isBadRequest());
    }
  }

}
