package com.emmsale.member.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.helper.MockMvcTestHelper;
import com.emmsale.member.application.MemberCareerService;
import com.emmsale.member.application.dto.MemberCareerInitialRequest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

@WebMvcTest(MemberApi.class)
class MemberApiTest extends MockMvcTestHelper {

  @MockBean
  private MemberCareerService memberCareerService;

  @Test
  @DisplayName("사용자 정보를 잘 저장하면, 204 no Content를 반환해준다.")
  void register() throws Exception {
    //given
    final List<Long> careerIds = List.of(1L, 2L);
    final String name = "우르";

    final MemberCareerInitialRequest request = new MemberCareerInitialRequest(name, careerIds);

    //when & then
    mockMvc.perform(post("/members")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent())
        .andDo(print());
  }
}
