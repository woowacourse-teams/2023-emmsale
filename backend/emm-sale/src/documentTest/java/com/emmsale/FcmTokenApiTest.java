package com.emmsale;

import com.emmsale.notification.api.FcmTokenApi;
import com.emmsale.notification.application.dto.FcmTokenRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(FcmTokenApi.class)
class FcmTokenApiTest extends MockMvcTestHelper {

  @Test
  @DisplayName("createFcmToken() : FcmToken이 새로 생성되거나 잘 수정되면 200 OK를 반환할 수 있다.")
  void test_createFcmToken() throws Exception {
    //given
    final RequestFieldsSnippet requestFields = PayloadDocumentation.requestFields(
        PayloadDocumentation.fieldWithPath("token").description("FcmToken 주세요"),
        PayloadDocumentation.fieldWithPath("memberId").description("FcmToken 주인의 ID")
    );

    final long memberId = 1L;
    final String token = "FCM 토큰";

    final FcmTokenRequest request = new FcmTokenRequest(token, memberId);

    Mockito.doNothing().when(fcmTokenRegisterService).registerFcmToken(ArgumentMatchers.any());

    //when & then
    mockMvc.perform(RestDocumentationRequestBuilders.post("/notifications/token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcResultHandlers.print())
        .andDo(MockMvcRestDocumentation.document("create-fcmToken", requestFields));
  }
}
