package com.emmsale.event.api;

import static java.lang.String.format;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.event.application.EventService;
import com.emmsale.event.application.dto.EventDetailResponse;
import com.emmsale.event.application.dto.ParticipantResponse;
import com.emmsale.helper.MockMvcTestHelper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

@WebMvcTest(EventApi.class)
class EventApiTest extends MockMvcTestHelper {

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
  @DisplayName("행사의 참여자를 전체 조회할 수 있다.")
  void findParticipants() throws Exception {
    //given
    final Long eventId = 1L;
    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("참여자 식별자"),
        fieldWithPath("[].memberId").type(JsonFieldType.NUMBER).description("member의 식별자"),
        fieldWithPath("[].name").type(JsonFieldType.STRING).description("member 이름"),
        fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("프로필 이미지 url"),
        fieldWithPath("[].description").type(JsonFieldType.STRING).description("한줄 자기 소개")
    );
    final List<ParticipantResponse> responses = List.of(
        new ParticipantResponse(1L, 1L, "스캇", "imageUrl",
            "토마토 던지는 사람"),
        new ParticipantResponse(2L, 2L, "홍실", "imageUrl",
            "토마토 맞는 사람")
    );

    when(eventService.findParticipants(eventId))
        .thenReturn(responses);

    //when && then
    mockMvc.perform(get(format("/events/%s/participants", eventId)))
        .andExpect(status().isOk())
        .andDo(document("find-participants", responseFields));
  }
}
