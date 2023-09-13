package com.emmsale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.message_room.api.RoomApi;
import com.emmsale.message_room.application.dto.RoomResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.RequestParametersSnippet;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(RoomApi.class)
class RoomApiTest extends MockMvcTestHelper {

  @Test
  @DisplayName("findAllRoom() : 사용자의 쪽지함을 성공적으로 조회하면 200 OK를 반환할 수 있다.")
  void test_findAllRoom() throws Exception {
    //given
    final String accessToken = "Bearer AccessToken";

    final RequestParametersSnippet requestParam = requestParameters(
        parameterWithName("member-id").description("조회할 사용자의 ID")
    );

    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("[].roomId").description("Room Id(String 타입의 UUID입니다)"),
        fieldWithPath("[].interlocutorId").description("쪽지를 주고받는 상대방 ID"),
        fieldWithPath("[].interlocutorName").description("쪽지를 주고받는 상대방의 이름"),
        fieldWithPath("[].recentlyMessage").description("최근 메시지 내용"),
        fieldWithPath("[].recentlyMessageTime").description("최근 메시지 시간")
    );

    final List<RoomResponse> roomResponses = List.of(
        new RoomResponse(UUID.randomUUID().toString(), 1L,"receiver1", "최근 메시지1", LocalDateTime.now()),
        new RoomResponse(UUID.randomUUID().toString(), 1L,"receiver2", "최근 메시지2", LocalDateTime.now().minusDays(2)),
        new RoomResponse(UUID.randomUUID().toString(), 1L,"receiver3", "최근 메시지3", LocalDateTime.now().minusDays(3))
    );

    when(roomQueryService.findAll(any(), anyLong()))
        .thenReturn(roomResponses);

    //when & then
    mockMvc.perform(MockMvcRequestBuilders.get("/rooms")
            .queryParam("member-id", "1")
            .header(HttpHeaders.AUTHORIZATION, accessToken))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("get-rooms", requestParam, responseFields));
  }
}
