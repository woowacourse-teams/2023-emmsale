package com.emmsale.career.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.career.application.CareerService;
import com.emmsale.member.application.MemberQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CareerApi.class)
class CareerApiTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CareerService careerService;

  @MockBean
  private MemberQueryService memberQueryService;

  @Test
  @DisplayName("커리어를 전제 조회할 수 있으면 200 OK를 반환한다.")
  void findAll() throws Exception {
    // when & then
    mockMvc.perform(get("/careers"))
        .andExpect(status().isOk());
  }
}
