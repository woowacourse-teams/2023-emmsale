package com.emmsale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
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
      fieldWithPath("organization").description("행사기관"),
      fieldWithPath("paymentType").description("유무료 여부(유료,무료,유무료)")
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
        List.of("imageUrl1", "imageUrl2"), "인프런", "유료");

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
        any(LocalDate.class), eq("2023-07-01"),
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
        .andDo(MockMvcRestDocumentation.document("find-events", requestParameters, responseFields));
  }
  
  @Test
  @DisplayName("이벤트를 성공적으로 업데이트하면 200, OK를 반환한다.")
  void updateEventTest() throws Exception {
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
    final long eventId = 1L;
    final Event event = EventFixture.인프콘_2023();
    
    final List<TagRequest> tags = Stream.of(TagFixture.백엔드(), TagFixture.안드로이드())
        .map(tag -> new TagRequest(tag.getName())).collect(Collectors.toList());
    
    final EventDetailRequest request = new EventDetailRequest(event.getName(),
        event.getLocation(), event.getInformationUrl(), event.getEventPeriod().getStartDate(),
        event.getEventPeriod().getEndDate(),
        event.getEventPeriod().getApplyStartDate(), event.getEventPeriod().getApplyEndDate(),
        tags, event.getImageUrl(), event.getType(), EventMode.ON_OFFLINE, PaymentType.FREE,
        "행사기관");
    
    final EventDetailResponse response = new EventDetailResponse(1L, request.getName(),
        request.getInformationUrl(), request.getStartDateTime(), request.getEndDateTime(),
        request.getApplyStartDateTime(), request.getApplyEndDateTime(),
        request.getLocation(), EventStatus.IN_PROGRESS.name(), EventStatus.ENDED.name(),
        tags.stream().map(TagRequest::getName).collect(Collectors.toList()),
        request.getImageUrl(), 10, 10, request.getType().toString(),
        List.of("imageUrl1", "imageUrl2"), "행사기관","유료");
    
    Mockito.when(eventService.updateEvent(eq(eventId), any(EventDetailRequest.class), any(), any()))
        .thenReturn(response);
    
    String contents = objectMapper.writeValueAsString(request);
    
    final RequestPartsSnippet requestPartsSnippet = requestParts(
        partWithName("images").description("이미지들").optional(),
        partWithName("request").description("행사 정보들"),
        partWithName("request.name").description("행사(Event) 이름").optional(),
        partWithName("request.location").description("행사(Event) 장소").optional(),
        partWithName("request.startDateTime").description("행사(Event) 시작일시").optional(),
        partWithName("request.endDateTime").description("행사(Event) 종료일시").optional(),
        partWithName("request.applyStartDateTime").description("행사(Event) 신청시작일시").optional(),
        partWithName("request.applyEndDateTime").description("행사(Event) 신청종료일시").optional(),
        partWithName("request.informationUrl").description("행사(Event) 상세 정보 URL").optional(),
        partWithName("request.tags[]").description("연관 태그명").optional(),
        partWithName("request.imageUrl").description("행사(Event) imageUrl").optional(),
        partWithName("request.type").description("Event 타입").optional(),
        partWithName("request.eventMode").description("행사 온오프라인 여부(ON_OFFLINE, OFFLINE, ONLINE)")
            .optional(),
        partWithName("request.paymentType").description("행사 유료 여부(PAID, FREE, FREE_PAID)")
            .optional(),
        partWithName("request.organization").description("행사 주최 기관").optional()
    );
    
    //when
    MockMultipartHttpServletRequestBuilder builder = multipart(HttpMethod.PUT, "/events/" + eventId)
        .file("images", image1.getBytes())
        .file("images", image2.getBytes())
        .file(new MockMultipartFile("request", "", "application/json", contents.getBytes(
            StandardCharsets.UTF_8)));
    
    final ResultActions result = mockMvc.perform(builder);
    
    //when & then
    result.andExpect(status().isOk())
        .andDo(MockMvcResultHandlers.print())
        .andDo(MockMvcRestDocumentation.document("update-event", requestPartsSnippet,
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
          "행사기관");
      
      final EventDetailResponse response = new EventDetailResponse(1L, request.getName(),
          request.getInformationUrl(), request.getStartDateTime(), request.getEndDateTime(),
          request.getApplyStartDateTime(), request.getApplyEndDateTime(),
          request.getLocation(), EventStatus.IN_PROGRESS.name(), EventStatus.ENDED.name(),
          tags.stream().map(TagRequest::getName).collect(Collectors.toList()),
          request.getImageUrl(), 10, 10, request.getType().toString(),
          List.of("imageUrl1", "imageUrl2"), "행사기관","무료");
      
      Mockito.when(eventService.addEvent(any(EventDetailRequest.class), any(), any()))
          .thenReturn(response);
      
      String contents = objectMapper.writeValueAsString(request);
      
      final RequestPartsSnippet requestPartsSnippet = requestParts(
          partWithName("images").description("이미지들").optional(),
          partWithName("request").description("행사 정보들"),
          partWithName("request.name").description("행사(Event) 이름").optional(),
          partWithName("request.location").description("행사(Event) 장소").optional(),
          partWithName("request.startDateTime").description("행사(Event) 시작일시").optional(),
          partWithName("request.endDateTime").description("행사(Event) 종료일시").optional(),
          partWithName("request.applyStartDateTime").description("행사(Event) 신청시작일시").optional(),
          partWithName("request.applyEndDateTime").description("행사(Event) 신청종료일시").optional(),
          partWithName("request.informationUrl").description("행사(Event) 상세 정보 URL").optional(),
          partWithName("request.tags[]").description("연관 태그명").optional(),
          partWithName("request.imageUrl").description("행사(Event) imageUrl").optional(),
          partWithName("request.type").description("Event 타입").optional(),
          partWithName("request.eventMode").description("행사 온오프라인 여부(ON_OFFLINE, OFFLINE, ONLINE)")
              .optional(),
          partWithName("request.paymentType").description("행사 유료 여부(PAID, FREE, FREE_PAID)")
              .optional(),
          partWithName("request.organization").description("행사 주최 기관").optional()
      );
      
      //when
      MockMultipartHttpServletRequestBuilder builder = multipart("/events")
          .file("images", image1.getBytes())
          .file("images", image2.getBytes())
          .file(new MockMultipartFile("request", "", "application/json", contents.getBytes(
              StandardCharsets.UTF_8)));
      
      final ResultActions result = mockMvc.perform(builder);
      
      //then
      result.andExpect(status().isCreated())
          .andDo(MockMvcResultHandlers.print())
          .andDo(MockMvcRestDocumentation.document("add-event", EVENT_DETAIL_RESPONSE_FILED,
              requestPartsSnippet));
    }
    
    @ParameterizedTest
    @NullSource
    @EmptySource
    @DisplayName("이름에 빈 값이 들어올 경우 400 BAD_REQUEST를 반환한다.")
    void addEventWithEmptyNameTest(final String eventName) throws Exception {
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
      final EventDetailRequest request = new EventDetailRequest(
          eventName, event.getLocation(), event.getInformationUrl(), event.getEventPeriod()
          .getStartDate(), event.getEventPeriod().getEndDate(),
          event.getEventPeriod().getApplyStartDate(), event.getEventPeriod().getApplyEndDate(),
          tags, event.getImageUrl(), event.getType(), event.getEventMode(),
          event.getPaymentType(), event.getOrganization());
      String contents = objectMapper.writeValueAsString(request);
      //when & then
      mockMvc.perform(multipart("/events")
              .file("images", image1.getBytes())
              .file("images", image2.getBytes())
              .file(new MockMultipartFile("request", "", "application/json", contents.getBytes(
                  StandardCharsets.UTF_8)))
          )
          .andExpect(status().isBadRequest());
    }
    
    @ParameterizedTest
    @NullSource
    @EmptySource
    @DisplayName("장소에 빈 값이 들어올 경우 400 BAD_REQUEST를 반환한다.")
    void addEventWithEmptyLocationTest(final String eventLocation) throws Exception {
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
      final EventDetailRequest request = new EventDetailRequest(
          event.getName(), eventLocation, event.getInformationUrl(), event.getEventPeriod()
          .getStartDate(), event.getEventPeriod().getEndDate(),
          event.getEventPeriod().getApplyStartDate(), event.getEventPeriod().getApplyEndDate(),
          tags, event.getImageUrl(), event.getType(), event.getEventMode(),
          event.getPaymentType(), event.getOrganization());
      String contents = objectMapper.writeValueAsString(request);
      //when & then
      mockMvc.perform(multipart("/events")
              .file("images", image1.getBytes())
              .file("images", image2.getBytes())
              .file(new MockMultipartFile("request", "", "application/json", contents.getBytes(
                  StandardCharsets.UTF_8)))
          )
          .andExpect(status().isBadRequest());
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"httpexample.com", "http:example.com", "http:/example.com",
        "httpsexample.com", "https:example.com", "https:/example.com"})
    @NullSource
    @DisplayName("상세 URL에 http:// 혹은 https://로 시작하지 않는 값이 들어올 경우 400 BAD_REQUEST를 반환한다.")
    void addEventWithInvalidInformationUrlTest(final String informationUrl) throws Exception {
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
      final EventDetailRequest request = new EventDetailRequest(
          event.getName(), event.getLocation(), informationUrl, event.getEventPeriod()
          .getStartDate(), event.getEventPeriod().getEndDate(),
          event.getEventPeriod().getApplyStartDate(), event.getEventPeriod().getApplyEndDate(),
          tags, event.getImageUrl(), event.getType(), event.getEventMode(),
          event.getPaymentType(), event.getOrganization());
      String contents = objectMapper.writeValueAsString(request);
      //when & then
      mockMvc.perform(multipart("/events")
              .file("images", image1.getBytes())
              .file("images", image2.getBytes())
              .file(new MockMultipartFile("request", "", "application/json", contents.getBytes(
                  StandardCharsets.UTF_8)))
          )
          .andExpect(status().isBadRequest());
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"23-01-01T12:00:00", "2023-1-01T12:00:00", "2023-01-1T12:00:00",
        "2023-01-01T2:00:00", "2023-01-01T12:0:00", "2023-01-01T12:00:0"})
    @NullSource
    @DisplayName("시작 일시에 null 혹은 다른 형식의 일시 값이 들어올 경우 400 BAD_REQUEST를 반환한다.")
    void addEventWithUnformattedStartDateTimeTest(final String startDateTime)
        throws Exception {
      //when & then
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
      
      Map<String, String> request = new HashMap<>();
      request.put("name", event.getName());
      request.put("location", event.getLocation());
      request.put("informationUrl", event.getInformationUrl());
      request.put("startDateTime", startDateTime);
      request.put("endDateTime", event.getEventPeriod().getEndDate().toString());
      request.put("applyStartDateTime", event.getEventPeriod().getApplyStartDate().toString());
      request.put("applyEndDateTime", event.getEventPeriod().getApplyEndDate().toString());
      request.put("imageUrl", event.getImageUrl());
      request.put("type", event.getType().name());
      request.put("eventMode", event.getEventMode().name());
      request.put("paymentType", event.getPaymentType().name());
      request.put("organization", event.getOrganization());
      
      String contents = objectMapper.writeValueAsString(request);
      //when & then
      mockMvc.perform(multipart("/events")
              .file("images", image1.getBytes())
              .file("images", image2.getBytes())
              .file(new MockMultipartFile("request", "", "application/json", contents.getBytes(
                  StandardCharsets.UTF_8)))
          )
          .andExpect(status().isBadRequest());
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"23-01-02T12:00:00", "2023-1-02T12:00:00", "2023-01-2T12:00:00",
        "2023-01-02T2:00:00", "2023-01-02T12:0:00", "2023-01-02T12:00:0"})
    @NullSource
    @DisplayName("종료 일시에 null 혹은 다른 형식의 일시 값이 들어올 경우 400 BAD_REQUEST를 반환한다.")
    void addEventWithUnformattedEndDateTimeTest(final String endDateTime) throws Exception {
      //when & then
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
      
      Map<String, String> request = new HashMap<>();
      request.put("name", event.getName());
      request.put("location", event.getLocation());
      request.put("informationUrl", event.getInformationUrl());
      request.put("startDateTime", event.getEventPeriod().getStartDate().toString());
      request.put("endDateTime", endDateTime);
      request.put("applyStartDateTime", event.getEventPeriod().getApplyStartDate().toString());
      request.put("applyEndDateTime", event.getEventPeriod().getApplyEndDate().toString());
      request.put("imageUrl", event.getImageUrl());
      request.put("type", event.getType().name());
      request.put("eventMode", event.getEventMode().name());
      request.put("paymentType", event.getPaymentType().name());
      request.put("organization", event.getOrganization());
      
      String contents = objectMapper.writeValueAsString(request);
      //when & then
      mockMvc.perform(multipart("/events")
              .file("images", image1.getBytes())
              .file("images", image2.getBytes())
              .file(new MockMultipartFile("request", "", "application/json", contents.getBytes(
                  StandardCharsets.UTF_8)))
          )
          .andExpect(status().isBadRequest());
    }
  }
}
