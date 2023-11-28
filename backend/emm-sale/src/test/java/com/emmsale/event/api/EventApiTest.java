package com.emmsale.event.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.event.application.EventCommandService;
import com.emmsale.event.application.EventQueryService;
import com.emmsale.resolver.MemberArgumentResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EventApi.class)
class EventApiTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MemberArgumentResolver memberArgumentResolver;
  @MockBean
  private EventQueryService eventQueryService;
  @MockBean
  private EventCommandService eventCommandService;

  @ParameterizedTest
  @ValueSource(strings = {"abcde", "00-0-0", "-1-1-1-1", "2023-02-30"})
  @DisplayName("유효하지 않은 값이 시작일 정보로 들어오면 예외를 반환한다.")
  void findEvents_start_date_fail(final String startDate) throws Exception {
    // when & then
    mockMvc.perform(get("/events")
            .param("start_date", startDate))
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @ValueSource(strings = {"abcde", "00-0-0", "-1-1-1-1", "2023-02-30"})
  @DisplayName("유효하지 않은 값이 종료일 값으로 들어오면 예외를 반환한다.")
  void findEvents_end_date_fail(final String endDate) throws Exception {
    // when & then
    mockMvc.perform(get("/events")
            .param("end_date", endDate))
        .andExpect(status().isBadRequest());
  }
}
