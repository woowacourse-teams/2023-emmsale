package com.emmsale.notification.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.helper.MockMvcTestHelper;
import com.emmsale.notification.application.dto.FcmTokenRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

@WebMvcTest(FcmTokenApi.class)
class FcmTokenApiTest extends MockMvcTestHelper {


  @Test
  @DisplayName("createFcmToken() : FcmToken이 새로 생성되거나 잘 수정되면 200 OK를 반환할 수 있다.")
  void test_createFcmToken() throws Exception {
    //given
    final RequestFieldsSnippet requestFields = requestFields(
        fieldWithPath("token").description("FcmToken 주세요"),
        fieldWithPath("memberId").description("FcmToken 주인의 ID")
    );

    final long memberId = 1L;
    final String token = "FCM 토큰";

    final FcmTokenRequest request = new FcmTokenRequest(token, memberId);

    doNothing().when(fcmTokenRegisterService).registerFcmToken(any());

    //when & then
    mockMvc.perform(post("/notifications/token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("create-fcmToken", requestFields));
  }
}
