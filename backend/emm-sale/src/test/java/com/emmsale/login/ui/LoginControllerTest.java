package com.emmsale.login.ui;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.helper.MockMvcTestHelper;
import com.emmsale.login.application.LoginService;
import com.emmsale.login.application.dto.TokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(LoginController.class)
class LoginControllerTest extends MockMvcTestHelper {

  @MockBean
  private LoginService loginService;

  @Test
  @DisplayName("code가 유효할 경우 200과 함께 TokenResponse를 반환해 준다.")
  void availableLoginTest() throws Exception {
    // given
    final String code = "code";
    final TokenResponse tokenResponse = new TokenResponse(1L, false, "access_token");

    given(loginService.createToken(code)).willReturn(tokenResponse);

    // when
    final ResultActions result = mockMvc.perform(
        get("/login/github/callback").param("code", code)
    );

    // then
    result.andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("code가 존재하지 않을 경우 400 BadRequest를 반환한다.")
  void illegalLoginTest() throws Exception {
    // when
    final ResultActions result = mockMvc.perform(get("/login/github/callback"));

    // then
    result.andExpect(status().isBadRequest())
        .andDo(print());
  }
}
