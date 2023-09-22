package com.emmsale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.member.domain.Member;
import com.emmsale.message_room.api.MessagesApi;
import com.emmsale.message_room.application.dto.MessageSendRequest;
import com.emmsale.message_room.application.dto.MessageSendResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

@WebMvcTest(MessagesApi.class)
public class MessagesApiTest extends MockMvcTestHelper {

  @Test
  @DisplayName("sendMessage() : 다른 사람에게 메시지를 보낸다.")
  void sendMessageTest() throws Exception {
    //given
    final RequestFieldsSnippet requestFields = requestFields(
        fieldWithPath("senderId").type(JsonFieldType.NUMBER).description("메시지 보내는 멤버의 id"),
        fieldWithPath("receiverId").type(JsonFieldType.NUMBER).description("메시지 받는 멤버의 id"),
        fieldWithPath("content").type(JsonFieldType.STRING).description("메시지 내용")
    );
    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("roomId").type(JsonFieldType.STRING).description("쪽지방의 id")
    );
    final Long senderId = 1L;
    final Long receiverId = 2L;
    final String content = "메시지 내용";
    final MessageSendRequest request = new MessageSendRequest(senderId, receiverId, content);
    final MessageSendResponse response = new MessageSendResponse("roomId");

    given(messageCommandService.sendMessage(any(MessageSendRequest.class), any(Member.class)))
        .willReturn(response);

    //when && then
    mockMvc.perform(post("/messages")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("send-message", requestFields, responseFields));
  }
}
