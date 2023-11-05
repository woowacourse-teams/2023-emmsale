package com.emmsale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.member.application.dto.MemberReferenceResponse;
import com.emmsale.member.domain.Member;
import com.emmsale.message_room.api.RoomApi;
import com.emmsale.message_room.application.dto.MessageResponse;
import com.emmsale.message_room.application.dto.RoomResponse;
import com.emmsale.message_room.domain.Message;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.restdocs.request.RequestParametersSnippet;

@WebMvcTest(RoomApi.class)
class RoomApiTest extends MockMvcTestHelper {

  private static final ResponseFieldsSnippet MESSAGES_RESPONSE_FIELDS = responseFields(
      fieldWithPath("[].id").description("메시지의 ID"),
      fieldWithPath("[].sender.id").description("메시지를 보낸 사람 ID"),
      fieldWithPath("[].sender.name").description("메시지를 보낸 사람의 이름"),
      fieldWithPath("[].sender.description").description("메시지를 보낸 사람의 한 줄 자기소개"),
      fieldWithPath("[].sender.imageUrl").description("메시지를 보낸 사람의 프로필 이미지 URL"),
      fieldWithPath("[].sender.githubUrl").description("메시지를 보낸 사람의 Github ID"),
      fieldWithPath("[].content").description("메시지 내용"),
      fieldWithPath("[].createdAt").description("메시지 보낸 시간")
  );
  private static final String accessToken = "Bearer AccessToken";

  @Test
  @DisplayName("findAllRoom() : 사용자의 쪽지함을 성공적으로 조회하면 200 OK를 반환할 수 있다.")
  void test_findAllRoom() throws Exception {
    //given
    final RequestParametersSnippet requestParam = requestParameters(
        parameterWithName("member-id").description("조회할 사용자의 ID")
    );

    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("[].roomId").description("Room Id(String 타입의 UUID입니다)"),
        fieldWithPath("[].interlocutor.id").type(JsonFieldType.NUMBER)
            .description("메세지를 주고받는 member의 식별자"),
        fieldWithPath("[].interlocutor.name").type(JsonFieldType.STRING)
            .description("메세지를 주고받는 member의 이름"),
        fieldWithPath("[].interlocutor.description").type(JsonFieldType.STRING)
            .description("메세지를 주고받는 member의 한줄 자기소개"),
        fieldWithPath("[].interlocutor.imageUrl").type(JsonFieldType.STRING)
            .description("메세지를 주고받는 member의 이미지 url"),
        fieldWithPath("[].interlocutor.githubUrl").type(JsonFieldType.STRING)
            .description("메세지를 주고받는 member의 github Url"),
        fieldWithPath("[].recentlyMessage.id").description("최근 메시지 내용"),
        fieldWithPath("[].recentlyMessage.content").description("최근 메시지 내용"),
        fieldWithPath("[].recentlyMessage.sender.id").type(JsonFieldType.NUMBER)
            .description("최근 메세지를 전송한 member의 식별자"),
        fieldWithPath("[].recentlyMessage.sender.name").type(JsonFieldType.STRING)
            .description("최근 메세지를 전송한 member의 이름"),
        fieldWithPath("[].recentlyMessage.sender.description").type(JsonFieldType.STRING)
            .description("최근 메세지를 전송한 member의 한줄 자기소개"),
        fieldWithPath("[].recentlyMessage.sender.imageUrl").type(JsonFieldType.STRING)
            .description("최근 메세지를 전송한 member의 이미지 url"),
        fieldWithPath("[].recentlyMessage.sender.githubUrl").type(JsonFieldType.STRING)
            .description("최근 메세지를 전송한 member의 github Url"),
        fieldWithPath("[].recentlyMessage.createdAt").description("최근 메시지 시간")
    );

    final Member member1 = new Member(1L, 3L, "https://github.image", "receiver1", "amaran-th");
    final Member member2 = new Member(2L, 4L, "https://github.image2", "receiver2", "amaran-th2");
    final Member member3 = new Member(3L, 8L, "https://github.image2", "receiver3", "amaran-th3");
    final Message message1 = new Message(1L, "최근 메시지1", member1, "ROOMID1111", LocalDateTime.now());
    final Message message2 = new Message(2L, "최근 메시지2", member2, "ROOMID2222",
        LocalDateTime.now().minusDays(2));
    final Message message3 = new Message(3L, "최근 메시지3", member3, "ROOMID3333",
        LocalDateTime.now().minusDays(3));

    final List<RoomResponse> roomResponses = List.of(
        new RoomResponse(UUID.randomUUID().toString(), MemberReferenceResponse.from(member1),
            MessageResponse.from(message1)),
        new RoomResponse(UUID.randomUUID().toString(), MemberReferenceResponse.from(member2),
            MessageResponse.from(message2)),
        new RoomResponse(UUID.randomUUID().toString(), MemberReferenceResponse.from(member3),
            MessageResponse.from(message3))
    );

    when(roomQueryService.findAll(any(), anyLong()))
        .thenReturn(roomResponses);

    //when & then
    mockMvc.perform(get("/rooms/overview")
            .queryParam("member-id", "1")
            .header(HttpHeaders.AUTHORIZATION, accessToken))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("get-rooms-overview", requestParam, responseFields));
  }

  @Test
  @DisplayName("findByRoomId() : Room Id로 쪽지방을 성공적으로 조회하면 200 OK를 반환할 수 있다.")
  void test_findByRoomId() throws Exception {
    //given

    final PathParametersSnippet pathParams = pathParameters(
        parameterWithName("room-id").description("조회할 Room UUID")
    );
    final Member member1 = new Member(1L, 3L, "https://github.image", "sender", "amaran-th");
    final Member member2 = new Member(2L, 4L, "https://github.image", "receiver", "amaran-th");
    final List<MessageResponse> messageResponses = List.of(
        new MessageResponse(1L, MemberReferenceResponse.from(member1), "내용1", LocalDateTime.now()),
        new MessageResponse(2L, MemberReferenceResponse.from(member2), "내용2", LocalDateTime.now()),
        new MessageResponse(3L, MemberReferenceResponse.from(member1), "내용3", LocalDateTime.now())
    );

    when(roomQueryService.findByRoomId(any(), any()))
        .thenReturn(messageResponses);

    //when & then
    mockMvc.perform(get("/rooms/{room-id}", 1L)
            .header(HttpHeaders.AUTHORIZATION, accessToken))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("get-rooms-roomId", MESSAGES_RESPONSE_FIELDS, pathParams));
  }

  @Test
  @DisplayName("findByInterlocutorIds() : 쪽지방 참여자들의 ID를 통해 Room을 성공적으로 조회하면 200 OK를 반환할 수 있다.")
  void test_findByInterlocutorIds() throws Exception {
    //given
    final RequestParametersSnippet requestParam = requestParameters(
        parameterWithName("receiver-id").description("쪽지방 참여 상대방 ID")
    );

    final Member member1 = new Member(1L, 3L, "https://github.image", "sender", "amaran-th");
    final Member member2 = new Member(2L, 4L, "https://github.image", "receiver", "amaran-th");

    final List<MessageResponse> messageResponses = List.of(
        new MessageResponse(1L, MemberReferenceResponse.from(member1), "내용1", LocalDateTime.now()),
        new MessageResponse(2L, MemberReferenceResponse.from(member2), "내용2", LocalDateTime.now()),
        new MessageResponse(3L, MemberReferenceResponse.from(member1), "내용3", LocalDateTime.now())
    );

    when(roomQueryService.findByInterlocutorIds(anyLong(), any()))
        .thenReturn(messageResponses);

    //when & then
    mockMvc.perform(get("/rooms")
            .queryParam("receiver-id", "1")
            .header(HttpHeaders.AUTHORIZATION, accessToken))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("get-rooms-interlocutorId", requestParam, MESSAGES_RESPONSE_FIELDS));
  }
}
