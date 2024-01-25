package com.emmsale;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.admin.login.api.AdminLoginApi;
import com.emmsale.admin.login.application.dto.AdminLoginRequest;
import com.emmsale.admin.login.application.dto.AdminTokenResponse;
import com.emmsale.login.api.LoginApi;
import com.emmsale.login.application.dto.TokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.RequestParametersSnippet;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest({LoginApi.class, AdminLoginApi.class})
class LoginApiTest extends MockMvcTestHelper {

  @Test
  @DisplayName("code가 유효할 경우 200과 함께 TokenResponse를 반환해 준다.")
  void availableLoginTest() throws Exception {
    // given
    final String code = "code";
    final TokenResponse tokenResponse = new TokenResponse(1L, false, "access_token");

    BDDMockito.given(loginService.createTokenByGithub(code)).willReturn(tokenResponse);

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

  @Test
  @DisplayName("id와 password가 유효할 경우 200과 함께 AdminTokenResponse를 반환해 준다.")
  void availableAdminLoginTest() throws Exception {
    // given
    final AdminLoginRequest request = new AdminLoginRequest("관리자 id", "관리자 password");
    final AdminTokenResponse adminTokenResponse = new AdminTokenResponse("access_token");

    BDDMockito.given(adminLoginService.createAdminToken(any())).willReturn(adminTokenResponse);

    final RequestFieldsSnippet requestFields = requestFields(
        fieldWithPath("id").type(JsonFieldType.STRING).description("관리자 로그인 id"),
        fieldWithPath("password").type(JsonFieldType.STRING).description("관리자 로그인 password")
    );

    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("accessToken").type(JsonFieldType.STRING).description("관리자 Access Token 값")
    );

    // when
    final ResultActions result = mockMvc.perform(
        post("/admin/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request))
    );

    // then
    result.andExpect(status().isOk())
        .andDo(print())
        .andDo(document("admin-login-snippet", requestFields, responseFields));
  }
}
