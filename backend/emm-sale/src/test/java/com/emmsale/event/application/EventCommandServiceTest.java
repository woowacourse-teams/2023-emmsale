package com.emmsale.event.application;

import static com.emmsale.event.EventFixture.AI_아이디어_공모전;
import static com.emmsale.event.EventFixture.AI_컨퍼런스;
import static com.emmsale.event.EventFixture.구름톤;
import static com.emmsale.event.EventFixture.모바일_컨퍼런스;
import static com.emmsale.event.EventFixture.안드로이드_컨퍼런스;
import static com.emmsale.event.EventFixture.웹_컨퍼런스;
import static com.emmsale.event.EventFixture.인프콘_2023;
import static com.emmsale.event.exception.EventExceptionType.NOT_FOUND_EVENT;
import static com.emmsale.event.exception.EventExceptionType.NOT_FOUND_TAG;
import static com.emmsale.event.exception.EventExceptionType.START_DATE_TIME_AFTER_END_DATE_TIME;
import static com.emmsale.image.ImageFixture.행사_이미지1;
import static com.emmsale.image.ImageFixture.행사_이미지2;
import static com.emmsale.image.ImageFixture.행사_이미지3;
import static com.emmsale.tag.TagFixture.AI;
import static com.emmsale.tag.TagFixture.IOS;
import static com.emmsale.tag.TagFixture.백엔드;
import static com.emmsale.tag.TagFixture.안드로이드;
import static com.emmsale.tag.TagFixture.프론트엔드;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;

import com.emmsale.event.application.dto.EventDetailRequest;
import com.emmsale.event.application.dto.EventResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventMode;
import com.emmsale.event.domain.EventTag;
import com.emmsale.event.domain.EventType;
import com.emmsale.event.domain.PaymentType;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.domain.repository.EventTagRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.image.domain.repository.ImageRepository;
import com.emmsale.notification.domain.Notification;
import com.emmsale.tag.application.dto.TagRequest;
import com.emmsale.tag.domain.Tag;
import com.emmsale.tag.domain.TagRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class EventCommandServiceTest extends ServiceIntegrationTestHelper {

  private static final EventResponse 인프콘_2023 = new EventResponse(null, "인프콘 2023",
      null, null, null, null, null, "코엑스", List.of("백엔드"),
      "이미지1", EventType.CONFERENCE.name(), List.of(), "인프런", PaymentType.PAID.getValue(),
      EventMode.OFFLINE.getValue());
  private static final EventResponse 웹_컨퍼런스 = new EventResponse(null, "웹 컨퍼런스", null,
      null, null,
      null, null, "코엑스", List.of("백엔드"), "이미지1", EventType.CONFERENCE.name(),
      List.of(), "주최기관", PaymentType.PAID.getValue(), EventMode.ONLINE.getValue());
  private static final EventResponse 안드로이드_컨퍼런스 = new EventResponse(null, "안드로이드 컨퍼런스",
      null, null, null, null, null, "코엑스", List.of("백엔드"),
      "이미지1", EventType.CONFERENCE.name(), List.of(), "주최기관", PaymentType.PAID.getValue(),
      EventMode.ONLINE.getValue());
  private static final EventResponse AI_컨퍼런스 = new EventResponse(null, "AI 컨퍼런스",
      null, null, null, null, null, "코엑스", List.of("백엔드"),
      "이미지1", EventType.CONFERENCE.name(), List.of(), "주최기관", PaymentType.PAID.getValue(),
      EventMode.ONLINE.getValue());
  private static final EventResponse 모바일_컨퍼런스 = new EventResponse(null, "모바일 컨퍼런스",
      null, null, null, null, null, "코엑스", List.of("백엔드"),
      "이미지1", EventType.CONFERENCE.name(), List.of(), "주최기관", PaymentType.PAID.getValue(),
      EventMode.ONLINE.getValue());
  private static final EventResponse AI_아이디어_공모전 = new EventResponse(null,
      "AI 아이디어 공모전", null, null, null, null, null, "코엑스",
      List.of("백엔드"), "이미지1", EventType.CONFERENCE.name(), List.of(), "주최기관",
      PaymentType.PAID.getValue(), EventMode.ONLINE.getValue());
  private static final EventResponse 구름톤 = new EventResponse(null, "구름톤", null,
      null, null, null, null, "코엑스", List.of("백엔드"),
      "이미지1", EventType.COMPETITION.name(), List.of(), "주최기관", PaymentType.PAID.getValue(),
      EventMode.ONLINE.getValue());

  @Autowired
  private EventCommandService eventCommandService;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private EventTagRepository eventTagRepository;
  @Autowired
  private TagRepository tagRepository;
  @Autowired
  private ImageRepository imageRepository;

  private List<MultipartFile> mockMultipartFiles;

  @BeforeEach
  void init() {
    final Tag 백엔드 = tagRepository.save(백엔드());
    final Tag 프론트엔드 = tagRepository.save(프론트엔드());
    final Tag 안드로이드 = tagRepository.save(안드로이드());
    final Tag IOS = tagRepository.save(IOS());
    final Tag AI = tagRepository.save(AI());

    final Event 인프콘_2023 = eventRepository.save(인프콘_2023());
    final Event AI_컨퍼런스 = eventRepository.save(AI_컨퍼런스());
    final Event 모바일_컨퍼런스 = eventRepository.save(모바일_컨퍼런스());
    final Event 안드로이드_컨퍼런스 = eventRepository.save(안드로이드_컨퍼런스());
    final Event 웹_컨퍼런스 = eventRepository.save(웹_컨퍼런스());
    final Event AI_아이디어_공모전 = eventRepository.save(AI_아이디어_공모전());
    final Event 구름톤 = eventRepository.save(구름톤());

    eventTagRepository.saveAll(List.of(
        new EventTag(인프콘_2023, 백엔드), new EventTag(인프콘_2023, 프론트엔드), new EventTag(인프콘_2023, 안드로이드),
        new EventTag(인프콘_2023, IOS), new EventTag(인프콘_2023, AI), new EventTag(AI_컨퍼런스, AI),
        new EventTag(모바일_컨퍼런스, 안드로이드), new EventTag(모바일_컨퍼런스, IOS), new EventTag(안드로이드_컨퍼런스, 안드로이드),
        new EventTag(웹_컨퍼런스, 백엔드), new EventTag(웹_컨퍼런스, 프론트엔드))
    );

    mockMultipartFiles = List.of(
        new MockMultipartFile(
            "picture",
            "picture.jpg",
            MediaType.TEXT_PLAIN_VALUE,
            "test data".getBytes()
        )
    );
    imageRepository.saveAll(List.of(
        행사_이미지1(인프콘_2023.getId()),
        행사_이미지2(인프콘_2023.getId()),
        행사_이미지3(인프콘_2023.getId())
    ));
    imageRepository.saveAll(List.of(
        행사_이미지1(웹_컨퍼런스.getId()),
        행사_이미지2(웹_컨퍼런스.getId())
    ));
    imageRepository.saveAll(List.of(
        행사_이미지1(안드로이드_컨퍼런스.getId())
    ));
  }

  @Nested
  class AddEvent {

    final List<TagRequest> tagRequests = List.of(
        new TagRequest(IOS().getName()),
        new TagRequest(AI().getName())
    );
    private final LocalDateTime beforeDateTime = LocalDateTime.now();
    private final LocalDateTime afterDateTime = beforeDateTime.plusDays(1);
    private final String eventName = "새로운 이름";
    private final String eventLocation = "새로운 장소";
    private final String eventInformationUrl = "https://새로운-상세-URL.com";
    private final String imageUrl = "https://image.com";
    private final PaymentType paymentType = PaymentType.FREE_PAID;
    private final EventMode eventMode = EventMode.ON_OFFLINE;
    private final EventType type = EventType.CONFERENCE;
    private final LocalDate now = LocalDate.now();
    private final String organization = "행사기관";

    @Test
    @DisplayName("이벤트를 성공적으로 저장한다.")
    void addEventTest() {
      //given
      final EventDetailRequest request = new EventDetailRequest(
          eventName,
          eventLocation,
          eventInformationUrl,
          beforeDateTime,
          afterDateTime,
          beforeDateTime,
          afterDateTime,
          tagRequests,
          type,
          eventMode,
          paymentType,
          organization
      );

      doNothing().when(firebaseCloudMessageClient)
          .sendMessageTo(any(Notification.class), anyLong());

      //when
      final EventResponse response = eventCommandService.addEvent(request,
          mockMultipartFiles);
      final Event savedEvent = eventRepository.findById(response.getId()).get();

      //then
      assertAll(
          () -> assertEquals(eventName, savedEvent.getName()),
          () -> assertEquals(eventLocation, savedEvent.getLocation()),
          () -> assertEquals(eventInformationUrl, savedEvent.getInformationUrl()),
          () -> assertEquals(beforeDateTime, savedEvent.getEventPeriod().getStartDate()),
          () -> assertEquals(afterDateTime, savedEvent.getEventPeriod().getEndDate())
      );
      assertThat(response.getTags())
          .containsAll(
              tagRequests.stream()
                  .map(TagRequest::getName)
                  .collect(Collectors.toList())
          );
    }

    @Test
    @DisplayName("행사 시작 일시가 행사 종료 일시 이후일 경우 EventException이 발생한다.")
    void addEventWithStartDateTimeAfterBeforeDateTimeTest() {
      //given
      final LocalDateTime startDateTime = afterDateTime;
      final LocalDateTime endDatetime = beforeDateTime;

      final EventDetailRequest request = new EventDetailRequest(
          eventName,
          eventLocation,
          eventInformationUrl,
          startDateTime,
          endDatetime,
          beforeDateTime,
          afterDateTime,
          tagRequests,
          type,
          eventMode,
          paymentType,
          organization
      );

      doNothing().when(firebaseCloudMessageClient)
          .sendMessageTo(any(Notification.class), anyLong());

      //when & then
      final EventException exception = assertThrowsExactly(EventException.class,
          () -> eventCommandService.addEvent(request, mockMultipartFiles));

      assertEquals(START_DATE_TIME_AFTER_END_DATE_TIME, exception.exceptionType());
    }

    @Test
    @DisplayName("Tag가 존재하지 않을 경우 EventException이 발생한다.")
    void addEventWithNotExistTagTest() {
      //given
      final List<TagRequest> tagRequests = List.of(
          new TagRequest(백엔드().getName()),
          new TagRequest(안드로이드().getName()),
          new TagRequest("존재하지 않는 태그")
      );

      final EventDetailRequest request = new EventDetailRequest(
          eventName,
          eventLocation,
          eventInformationUrl,
          beforeDateTime,
          afterDateTime,
          beforeDateTime,
          afterDateTime,
          tagRequests,
          type,
          eventMode,
          paymentType,
          organization
      );

      doNothing().when(firebaseCloudMessageClient)
          .sendMessageTo(any(Notification.class), anyLong());

      //when & then
      final EventException exception = assertThrowsExactly(EventException.class,
          () -> eventCommandService.addEvent(request, mockMultipartFiles));

      assertEquals(NOT_FOUND_TAG, exception.exceptionType());
    }
  }

  @Nested
  class UpdateEvent {

    final List<TagRequest> newTagRequests = List.of(
        new TagRequest(IOS().getName()),
        new TagRequest(AI().getName())
    );
    private final LocalDateTime beforeDateTime = LocalDateTime.now();
    private final LocalDateTime afterDateTime = beforeDateTime.plusDays(1);
    private final String newName = "새로운 이름";
    private final String newLocation = "새로운 장소";
    private final String newInformationUrl = "https://새로운-상세-URL.com";
    private final LocalDate now = LocalDate.now();
    private final PaymentType paymentType = PaymentType.FREE_PAID;
    private final EventMode eventMode = EventMode.ON_OFFLINE;
    private final String organization = "행사기관";

    @Test
    @DisplayName("이벤트를 성공적으로 업데이트한다.")
    void updateEventTest() {
      //given
      final LocalDateTime newStartDateTime = beforeDateTime;
      final LocalDateTime newEndDateTime = afterDateTime;

      final EventDetailRequest updateRequest = new EventDetailRequest(
          newName,
          newLocation,
          newInformationUrl,
          beforeDateTime,
          afterDateTime,
          beforeDateTime,
          afterDateTime,
          newTagRequests,
          EventType.CONFERENCE,
          eventMode,
          paymentType,
          organization
      );

      final Event event = eventRepository.save(인프콘_2023());
      final Long eventId = event.getId();

      //when
      final EventResponse response = eventCommandService.updateEvent(eventId, updateRequest,
          mockMultipartFiles);
      final Event updatedEvent = eventRepository.findById(eventId).get();

      //then
      assertAll(
          () -> assertEquals(newName, updatedEvent.getName()),
          () -> assertEquals(newLocation, updatedEvent.getLocation()),
          () -> assertEquals(newStartDateTime, updatedEvent.getEventPeriod().getStartDate()),
          () -> assertEquals(newEndDateTime, updatedEvent.getEventPeriod().getEndDate()),
          () -> assertEquals(newInformationUrl, updatedEvent.getInformationUrl())
      );
      assertThat(response.getTags())
          .containsAll(
              newTagRequests.stream()
                  .map(TagRequest::getName)
                  .collect(Collectors.toList())
          );
    }

    @Test
    @DisplayName("업데이트할 이벤트가 존재하지 않을 경우 EventException이 발생한다.")
    void updateEventWithNotExistsEventTest() {
      //given
      final long notExistsEventId = 0L;

      final EventDetailRequest updateRequest = new EventDetailRequest(
          newName,
          newLocation,
          newInformationUrl,
          beforeDateTime,
          afterDateTime,
          beforeDateTime,
          afterDateTime,
          newTagRequests,
          EventType.CONFERENCE,
          eventMode,
          paymentType,
          organization
      );

      //when & then
      final EventException exception = assertThrowsExactly(EventException.class,
          () -> eventCommandService.updateEvent(notExistsEventId, updateRequest,
              mockMultipartFiles));

      assertEquals(NOT_FOUND_EVENT, exception.exceptionType());
    }

    @Test
    @DisplayName("행사 시작 일시가 행사 종료 일시 이후일 경우 EventException이 발생한다.")
    void updateEventWithStartDateTimeAfterBeforeDateTimeTest() {
      //given
      final LocalDateTime newStartDateTime = afterDateTime;
      final LocalDateTime newEndDateTime = beforeDateTime;

      final EventDetailRequest updateRequest = new EventDetailRequest(
          newName,
          newLocation,
          newInformationUrl,
          newStartDateTime,
          newEndDateTime,
          beforeDateTime,
          afterDateTime,
          newTagRequests,
          EventType.CONFERENCE,
          eventMode,
          paymentType,
          organization
      );

      final Event event = eventRepository.save(인프콘_2023());
      final Long eventId = event.getId();

      //when & then
      final EventException exception = assertThrowsExactly(EventException.class,
          () -> eventCommandService.updateEvent(eventId, updateRequest, mockMultipartFiles));

      assertEquals(START_DATE_TIME_AFTER_END_DATE_TIME, exception.exceptionType());
    }

    @Test
    @DisplayName("Tag가 존재하지 않을 경우 EventException이 발생한다.")
    void updateEventWithNotExistTagTest() {
      //given
      final List<TagRequest> newTagRequests = List.of(
          new TagRequest("존재하지 않는 태그")
      );

      final EventDetailRequest updateRequest = new EventDetailRequest(
          newName,
          newLocation,
          newInformationUrl,
          beforeDateTime,
          afterDateTime,
          beforeDateTime,
          afterDateTime,
          newTagRequests,
          EventType.CONFERENCE,
          eventMode,
          paymentType,
          organization
      );

      final Event event = eventRepository.save(인프콘_2023());
      final Long eventId = event.getId();

      //when & then
      final EventException exception = assertThrowsExactly(EventException.class,
          () -> eventCommandService.updateEvent(eventId, updateRequest, mockMultipartFiles));

      assertEquals(NOT_FOUND_TAG, exception.exceptionType());
    }
  }

  @Nested
  class DeleteEvent {

    @Test
    @DisplayName("이벤트를 성공적으로 삭제한다.")
    void deleteEventTest() {
      //given
      final Event event = eventRepository.save(인프콘_2023());
      final Long eventId = event.getId();

      //when
      eventCommandService.deleteEvent(eventId);

      //then
      assertFalse(eventRepository.findById(eventId).isPresent());
    }

    @Test
    @DisplayName("삭제할 이벤트가 존재하지 않을 경우 EventException이 발생한다.")
    void deleteEventWithNotExistsEventTest() {
      //given
      final long notExistsEventId = 0L;

      //when & then
      final EventException exception = assertThrowsExactly(EventException.class,
          () -> eventCommandService.deleteEvent(notExistsEventId));

      assertEquals(NOT_FOUND_EVENT, exception.exceptionType());
    }
  }

}