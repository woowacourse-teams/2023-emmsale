package com.emmsale.login.api;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.helper.MockMvcTestHelper;
import com.emmsale.login.application.LoginService;
import com.emmsale.login.application.dto.TokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.RequestParametersSnippet;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(LoginApi.class)
class LoginApiTest extends MockMvcTestHelper {

  @MockBean
  private LoginService loginService;

  @Test
  @DisplayName("code가 유효할 경우 200과 함께 TokenResponse를 반환해 준다.")
  void availableLoginTest() throws Exception {
    // given
    final String code = "code";
    final TokenResponse tokenResponse = new TokenResponse(1L, false, "access_token");

    given(loginService.createToken(code)).willReturn(tokenResponse);

    final RequestParametersSnippet requestFields = requestParameters(
        parameterWithName("code").description("깃허브 로그인 코드")
    );

    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("멤버 id"),
        fieldWithPath("onboarded").type(JsonFieldType.BOOLEAN).description("온보딩 수행 여부"),
        fieldWithPath("accessToken").type(JsonFieldType.STRING).description("Access Token 값")
    );

    // when
    final ResultActions result = mockMvc.perform(
        post("/login/github/callback").param("code", code)
    );

    // then
    result.andExpect(status().isOk())
        .andDo(document("login-snippet", requestFields, responseFields));
  }

  @Test
  @DisplayName("code가 존재하지 않을 경우 400 BadRequest를 반환한다.")
  void illegalLoginTest() throws Exception {
    // when
    final ResultActions result = mockMvc.perform(post("/login/github/callback"));

    // then
    result.andExpect(status().isBadRequest())
        .andDo(print());
  }
}
