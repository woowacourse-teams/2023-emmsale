package com.emmsale.member.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.helper.MockMvcTestHelper;
import com.emmsale.member.application.MemberCareerService;
import com.emmsale.member.application.dto.MemberCareerAddRequest;
import com.emmsale.member.application.dto.MemberCareerDeleteRequest;
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
  @DisplayName("사용자 정보를 잘 저장하면, 204 no Content를 반환해줄 수 있다.")
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

  @Test
  @DisplayName("내 명함에서 활동이력들을 성공적으로 추가하면, 201 Created를 반환해줄 수 있다.")
  void addCareer() throws Exception {
    //given
    final List<Long> careerIds = List.of(4L, 5L, 6L);
    final MemberCareerAddRequest request = new MemberCareerAddRequest(careerIds);

    //when & then
    mockMvc.perform(post("/members/careers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(print());
  }

  @Test
  @DisplayName("내 명함에서 활동이력들을 성공적으로 삭제하면, 200 OK를 반환해줄 수 있다.")
  void test_deleteCareer() throws Exception {
    //given
    final List<Long> careerIds = List.of(1L, 2L);
    final MemberCareerDeleteRequest request = new MemberCareerDeleteRequest(careerIds);

    //when & then
    mockMvc.perform(delete("/members/careers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(print());
  }
}
