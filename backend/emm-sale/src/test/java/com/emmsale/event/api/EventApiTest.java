package com.emmsale.event.api;

import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.event.EventFixture;
import com.emmsale.event.application.EventService;
import com.emmsale.event.application.dto.EventCancelParticipateRequest;
import com.emmsale.event.application.dto.EventDetailRequest;
import com.emmsale.event.application.dto.EventDetailResponse;
import com.emmsale.event.application.dto.EventParticipateRequest;
import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.application.dto.ParticipantResponse;
import com.emmsale.event.application.dto.ParticipateUpdateRequest;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventStatus;
import com.emmsale.event.domain.EventType;
import com.emmsale.helper.MockMvcTestHelper;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.RequestParametersSnippet;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(EventApi.class)
class EventApiTest extends MockMvcTestHelper {

  private static final ResponseFieldsSnippet EVENT_DETAIL_RESPONSE_FILED = responseFields(
      fieldWithPath("id").type(JsonFieldType.NUMBER).description("event 식별자"),
      fieldWithPath("name").type(JsonFieldType.STRING).description("envent 이름"),
      fieldWithPath("informationUrl").type(JsonFieldType.STRING).description("상세정보 url"),
      fieldWithPath("startDate").type(JsonFieldType.STRING).description("시작일자"),
      fieldWithPath("endDate").type(JsonFieldType.STRING).description("종료일자"),
      fieldWithPath("location").type(JsonFieldType.STRING).description("장소"),
      fieldWithPath("status").type(JsonFieldType.STRING).description("진행상태"),
      fieldWithPath("tags[]").type(JsonFieldType.ARRAY).description("태그들"),
      fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("이미지 Url(포스터)"),
      fieldWithPath("remainingDays").type(JsonFieldType.NUMBER).description("시작일로 부터 D-day"),
      fieldWithPath("type").type(JsonFieldType.STRING).description("event의 타입"));

  @MockBean
  private EventService eventService;

  @Test
  @DisplayName("컨퍼런스의 상세정보를 조회할 수 있다.")
  void findEvent() throws Exception {
    //given
    final Long eventId = 1L;
    final EventDetailResponse eventDetailResponse = new EventDetailResponse(eventId, "인프콘 2023",
        "http://infcon.com", LocalDateTime.of(2023, 8, 15, 12, 0),
        LocalDateTime.of(2023, 8, 15, 12, 0), "코엑스", "예정", List.of("코틀린", "백엔드", "안드로이드"),
        "https://www.image.com", 2, EventType.COMPETITION.toString());

    when(eventService.findEvent(anyLong(), any())).thenReturn(eventDetailResponse);

    //when
    mockMvc.perform(get("/events/" + eventId)).andExpect(status().isOk())
        .andDo(document("find-event", EVENT_DETAIL_RESPONSE_FILED));
  }

  @Test
  @DisplayName("Event에 참여게시글을 추가할 수 있다.")
  void participateEvent() throws Exception {
    //given
    final Long eventId = 1L;
    final Long memberId = 2L;
    final Long participantId = 3L;
    final String content = "함께 해요 게시글의 내용";
    final EventParticipateRequest request = new EventParticipateRequest(memberId, content);
    final String fakeAccessToken = "Bearer accessToken";

    final RequestFieldsSnippet requestFields = requestFields(
        fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("멤버 식별자"),
        fieldWithPath("content").type(JsonFieldType.STRING)
            .description("함께 해요 게시글의 내용(공백 불가, 255자 최대)")
    );

    when(eventService.participate(any(), any(), any())).thenReturn(participantId);

    //when
    mockMvc.perform(
            post("/events/{eventId}/participants", eventId).header("Authorization", fakeAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
        .andExpect(header().string("Location",
            format("/events/%s/participants/%s", eventId, participantId)))
        .andDo(document("participate-event", requestFields));
  }

  @Test
  @DisplayName("Event에 사용자를 참여자 목록에서 제거할 수 있다.")
  void cancelParticipateEvent() throws Exception {
    //given
    final Long eventId = 1L;
    final Long memberId = 2L;
    final EventCancelParticipateRequest request = new EventCancelParticipateRequest(memberId);
    final String fakeAccessToken = "Bearer accessToken";

    final RequestParametersSnippet requestParameters = requestParameters(
        parameterWithName("member-id").description("멤버 식별자")
    );

    //when
    mockMvc.perform(delete(format("/events/%s/participants?member-id=%s", eventId, memberId))
            .header("Authorization", fakeAccessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent())
        .andDo(document("participate-event-cancel", requestParameters));
  }

  @Test
  @DisplayName("특정 카테고리의 행사 목록을 조회할 수 있으면 200 OK를 반환한다.")
  void findEvents() throws Exception {
    // given
    final RequestParametersSnippet requestParameters = requestParameters(
        parameterWithName("category").description("행사 카테고리(CONFERENCE, COMPETITION)"),
        parameterWithName("start_date").description("필터링하려는 기간의 시작일(yyyy-mm-dd)(option)")
            .optional(),
        parameterWithName("end_date").description("필터링하려는 기간의 끝일(yyyy-mm-dd)(option)").optional(),
        parameterWithName("tags").description("필터링하려는 태그(option)").optional(),
        parameterWithName("statuses").description("필터링하려는 상태(UPCOMING, IN_PROGRESS, ENDED)(option)")
            .optional()
    );

    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("행사 id"),
        fieldWithPath("[].name").type(JsonFieldType.STRING).description("행사명"),
        fieldWithPath("[].startDate").type(JsonFieldType.STRING)
            .description("행사 시작일(yyyy:MM:dd:HH:mm:ss)"),
        fieldWithPath("[].endDate").type(JsonFieldType.STRING)
            .description("행사 종료일(yyyy:MM:dd:HH:mm:ss)"),
        fieldWithPath("[].tags[]").type(JsonFieldType.ARRAY)
            .description("행사 태그 목록"),
        fieldWithPath("[].status").type(JsonFieldType.STRING).description("행사 진행 상황"),
        fieldWithPath("[].remainingDays").type(JsonFieldType.NUMBER).description("행사 시작일까지 남은 일 수"),
        fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("행사 이미지 URL")
    );

    final List<EventResponse> eventResponses = List.of(
        new EventResponse(1L, "인프콘 2023", LocalDateTime.parse("2023-06-03T12:00:00"),
            LocalDateTime.parse("2023-09-03T12:00:00"),
            List.of("백엔드", "프론트엔드", "안드로이드", "IOS", "AI"), "진행 중",
            "https://biz.pusan.ac.kr/dext5editordata/2022/08/20220810_160546511_10103.jpg",
            3),
        new EventResponse(5L, "웹 컨퍼런스", LocalDateTime.parse("2023-07-03T12:00:00"),
            LocalDateTime.parse("2023-08-03T12:00:00"), List.of("백엔드", "프론트엔드"), "진행 중",
            "https://biz.pusan.ac.kr/dext5editordata/2022/08/20220810_160546511_10103.jpg",
            3),
        new EventResponse(2L, "AI 컨퍼런스", LocalDateTime.parse("2023-07-22T12:00:00"),
            LocalDateTime.parse("2023-07-30T12:00:00"), List.of("AI"), "진행 예정",
            "https://biz.pusan.ac.kr/dext5editordata/2022/08/20220810_160546511_10103.jpg",
            3)

    );

    when(eventService.findEvents(any(EventType.class), any(LocalDate.class), eq("2023-07-01"),
        eq("2023-07-31"),
        eq(null), any())).thenReturn(eventResponses);

    // when & then
    mockMvc.perform(get("/events")
            .param("category", "CONFERENCE")
            .param("start_date", "2023-07-01")
            .param("end_date", "2023-07-31")
            .param("statuses", "UPCOMING,IN_PROGRESS")
        )
        .andExpect(status().isOk())
        .andDo(document("find-events", requestParameters, responseFields));
  }

  @Test
  @DisplayName("행사의 참여 게시글을 전체 조회할 수 있다.")
  void findParticipants() throws Exception {
    //given
    final Long eventId = 1L;
    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("참여자 식별자"),
        fieldWithPath("[].memberId").type(JsonFieldType.NUMBER).description("member의 식별자"),
        fieldWithPath("[].name").type(JsonFieldType.STRING).description("member 이름"),
        fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("프로필 이미지 url"),
        fieldWithPath("[].description").type(JsonFieldType.STRING).description("한줄 자기 소개"),
        fieldWithPath("[].content").type(JsonFieldType.STRING).description("함께해요 게시글 내용"),
        fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("함께해요 게시글 작성 날짜"),
        fieldWithPath("[].updatedAt").type(JsonFieldType.STRING).description("함께해요 게시글 수정 날짜")
    );
    final List<ParticipantResponse> responses = List.of(
        new ParticipantResponse(1L, 1L, "스캇", "imageUrl", "토마토 던지는 사람", "저랑 같이 컨퍼런스 갈 사람",
            LocalDate.of(2023, 7, 15), LocalDate.of(2023, 7, 15)),
        new ParticipantResponse(2L, 2L, "홍실", "imageUrl", "토마토 맞는 사람", "스캇 말고 저랑 갈 사람",
            LocalDate.of(2023, 7, 22), LocalDate.of(2023, 7, 22))
    );

    when(eventService.findParticipants(eventId)).thenReturn(responses);

    //when && then
    mockMvc.perform(get(format("/events/%s/participants", eventId))).andExpect(status().isOk())
        .andDo(document("find-participants", responseFields));
  }

  @Test
  @DisplayName("Event에 참여게시글을 수정할 수 있다.")
  void updateParticipate() throws Exception {
    //given
    final Long eventId = 1L;
    final Long participantId = 3L;
    final String content = "함께 해요 게시글의 내용";
    final ParticipateUpdateRequest request = new ParticipateUpdateRequest(content);
    final String fakeAccessToken = "Bearer accessToken";

    final RequestFieldsSnippet requestFields = requestFields(
        fieldWithPath("content").type(JsonFieldType.STRING)
            .description("함께 해요 게시글의 내용(공백 불가, 255자 최대)")
    );

    //when
    mockMvc.perform(
            put("/events/{eventId}/participants/{participantId}", eventId, participantId)
                .header("Authorization", fakeAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("update-participate", requestFields));

    verify(eventService).updateParticipant(any(), any(), any(), any());
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
        event.getInformationUrl(), event.getStartDate(), event.getEndDate(),
        event.getSubscriptionStartDate(), event.getSubscriptionEndDate(), tags,
        event.getImageUrl(), event.getType());

    final EventDetailResponse response = new EventDetailResponse(eventId, request.getName(),
        request.getInformationUrl(), request.getStartDateTime(), request.getEndDateTime(),
        request.getLocation(), EventStatus.IN_PROGRESS.name(),
        tags.stream().map(TagRequest::getName).collect(Collectors.toList()), request.getImageUrl(),
        10, request.getType().toString());

    when(eventService.updateEvent(any(), any(), any())).thenReturn(response);

    final RequestFieldsSnippet requestFields = requestFields(
        fieldWithPath("name").type(JsonFieldType.STRING).description("행사(Event) 이름"),
        fieldWithPath("location").type(JsonFieldType.STRING).description("행사(Event) 장소"),
        fieldWithPath("startDateTime").type(JsonFieldType.STRING).description("행사(Event) 시작일시"),
        fieldWithPath("endDateTime").type(JsonFieldType.STRING).description("행사(Event) 종료일시"),
        fieldWithPath("subscriptionStartDateTime").type(JsonFieldType.STRING)
            .description("행사(Event) 신청시작일시").optional(),
        fieldWithPath("subscriptionEndDateTime").type(JsonFieldType.STRING)
            .description("행사(Event) 신청종료일시").optional(),
        fieldWithPath("informationUrl").type(JsonFieldType.STRING)
            .description("행사(Event) 상세 정보 URL"),
        fieldWithPath("tags[].name").type(JsonFieldType.STRING).description("연관 태그명"),
        fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("행사(Event) 이미지url"),
        fieldWithPath("type").type(JsonFieldType.STRING).description("행사(Event) 타입")
    );

    //when
    final ResultActions result = mockMvc.perform(
        put("/events/" + eventId).contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)));

    //then
    result.andExpect(status().isOk()).andDo(print())
        .andDo(document("update-event", requestFields, EVENT_DETAIL_RESPONSE_FILED));
  }

  @Test
  @DisplayName("이벤트를 성공적으로 삭제하면 204, NO_CONTENT를 반환한다.")
  void deleteEventTest() throws Exception {
    //given
    final long eventId = 1L;
    final Event event = EventFixture.인프콘_2023();

    doNothing().when(eventService).deleteEvent(eventId);
    //when
    final ResultActions result = mockMvc.perform(delete("/events/" + eventId));

    //then
    result.andExpect(status().isNoContent()).andDo(print()).andDo(document("delete-event"));
  }

  @Test
  @DisplayName("이미 Event에 멤버가 참여헀는지 확인할 수 있다.")
  void isAlreadyParticipate() throws Exception {
    //given
    final Long memberId = 2L;
    final Long eventId = 3L;
    given(eventService.isAlreadyParticipate(eventId, memberId)).willReturn(true);

    //when && then
    mockMvc.perform(
            get("/events/{eventId}/participants/already-participate?member-id={memberId}"
                , eventId, memberId)
        )
        .andExpect(status().isOk())
        .andDo(document("check-already-participate"));
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
          event.getLocation(), event.getInformationUrl(), event.getStartDate(), event.getEndDate(),
          event.getSubscriptionStartDate(), event.getSubscriptionEndDate(),
          tags, event.getImageUrl(), event.getType());

      final EventDetailResponse response = new EventDetailResponse(1L, request.getName(),
          request.getInformationUrl(), request.getStartDateTime(), request.getEndDateTime(),
          request.getLocation(), EventStatus.IN_PROGRESS.name(),
          tags.stream().map(TagRequest::getName).collect(Collectors.toList()),
          request.getImageUrl(), 10, request.getType().toString());

      when(eventService.addEvent(any(), any())).thenReturn(response);

      final RequestFieldsSnippet requestFields = requestFields(
          fieldWithPath("name").type(JsonFieldType.STRING).description("행사(Event) 이름"),
          fieldWithPath("location").type(JsonFieldType.STRING).description("행사(Event) 장소"),
          fieldWithPath("startDateTime").type(JsonFieldType.STRING).description("행사(Event) 시작일시"),
          fieldWithPath("endDateTime").type(JsonFieldType.STRING).description("행사(Event) 종료일시"),
          fieldWithPath("subscriptionStartDateTime").type(JsonFieldType.STRING)
              .description("행사(Event) 신청시작일시").optional(),
          fieldWithPath("subscriptionEndDateTime").type(JsonFieldType.STRING)
              .description("행사(Event) 신청종료일시").optional(),
          fieldWithPath("informationUrl").type(JsonFieldType.STRING)
              .description("행사(Event) 상세 정보 URL"),
          fieldWithPath("tags[].name").type(JsonFieldType.STRING).description("연관 태그명"),
          fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("행사(Event) imageUrl"),
          fieldWithPath("type").type(JsonFieldType.STRING).description("Event 타입"));

      //when
      final ResultActions result = mockMvc.perform(
          post("/events").contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(objectMapper.writeValueAsString(request)));

      //then
      result.andExpect(status().isCreated()).andDo(print())
          .andDo(document("add-event", requestFields, EVENT_DETAIL_RESPONSE_FILED));
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
          event.getInformationUrl(), event.getStartDate(), event.getEndDate(),
          event.getSubscriptionStartDate(), event.getSubscriptionEndDate(), tags, null,
          EventType.COMPETITION);

      //when
      final ResultActions result = mockMvc.perform(
          post("/events").contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(objectMapper.writeValueAsString(request)));

      //then
      result.andExpect(status().isBadRequest());
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
          event.getInformationUrl(), event.getStartDate(), event.getEndDate(),
          event.getSubscriptionStartDate(), event.getSubscriptionEndDate(), tags,
          event.getImageUrl(), event.getType());

      //when
      final ResultActions result = mockMvc.perform(
          post("/events").contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(objectMapper.writeValueAsString(request)));

      //then
      result.andExpect(status().isBadRequest());
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
          event.getLocation(), informationUrl, event.getStartDate(), event.getEndDate(),
          event.getSubscriptionStartDate(), event.getSubscriptionEndDate(), tags,
          event.getImageUrl(), event.getType());

      //when
      final ResultActions result = mockMvc.perform(
          post("/events").contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(objectMapper.writeValueAsString(request)));

      //then
      result.andExpect(status().isBadRequest());
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
          post("/events").contentType(MediaType.APPLICATION_JSON_VALUE).content(request));

      //then
      result.andExpect(status().isBadRequest());
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
          post("/events")
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(request)
      );

      //then
      result.andExpect(status().isBadRequest());
    }
  }

}
