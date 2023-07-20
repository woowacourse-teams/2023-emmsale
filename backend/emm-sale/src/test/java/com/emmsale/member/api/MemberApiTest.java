package com.emmsale.member.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.helper.MockMvcTestHelper;
import com.emmsale.member.application.MemberCareerService;
import com.emmsale.member.application.dto.MemberActivityResponse;
import com.emmsale.member.application.dto.MemberCareerAddRequest;
import com.emmsale.member.application.dto.MemberCareerDeleteRequest;
import com.emmsale.member.application.dto.MemberCareerInitialRequest;
import com.emmsale.member.application.dto.MemberCareerResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

@WebMvcTest(MemberApi.class)
class MemberApiTest extends MockMvcTestHelper {

  @MockBean
  private MemberCareerService memberCareerService;

  private static final ResponseFieldsSnippet RESPONSE_FIELDS = responseFields(

      fieldWithPath("[].activityName").type(JsonFieldType.STRING).description("career 분류"),
      fieldWithPath("[].memberActivityResponses[].id").type(JsonFieldType.NUMBER)
          .description("career id"),
      fieldWithPath("[].memberActivityResponses[].name").type(JsonFieldType.STRING)
          .description("career 이름")
  );

  private static final RequestFieldsSnippet REQUEST_FIELDS = requestFields(
      fieldWithPath("careerIds").description("활동 id들"));

  @Test
  @DisplayName("사용자 정보를 잘 저장하면, 204 no Content를 반환해줄 수 있다.")
  void register() throws Exception {
    //given
    final List<Long> careerIds = List.of(1L, 2L);
    final String name = "우르";

    final MemberCareerInitialRequest request = new MemberCareerInitialRequest(name, careerIds);

    final RequestFieldsSnippet REQUEST_FIELDS = requestFields(
        fieldWithPath("careerIds").description("활동 id들"),
        fieldWithPath("name").description("사용자 이름"));

    //when & then
    mockMvc.perform(post("/members")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent())
        .andDo(print())
        .andDo(document("initial-register-member", REQUEST_FIELDS));
  }

  @Test
  @DisplayName("내 명함에서 활동이력들을 성공적으로 추가하면, 201 Created를 반환해줄 수 있다.")
  void addCareer() throws Exception {
    //given
    final List<Long> careerIds = List.of(4L, 5L, 6L);
    final MemberCareerAddRequest request = new MemberCareerAddRequest(careerIds);

    final List<MemberCareerResponse> memberCareerResponses = List.of(
        new MemberCareerResponse("동아리",
            List.of(
                new MemberActivityResponse(1L, "YAPP"),
                new MemberActivityResponse(2L, "DND"),
                new MemberActivityResponse(3L, "nexters")
            )),
        new MemberCareerResponse("컨퍼런스",
            List.of(
                new MemberActivityResponse(4L, "인프콘")
            )),
        new MemberCareerResponse("교육",
            List.of(
                new MemberActivityResponse(5L, "우아한테크코스")
            )),
        new MemberCareerResponse("직무",
            List.of(
                new MemberActivityResponse(6L, "Backend")
            ))
    );

    when(memberCareerService.addCareer(any(), any()))
        .thenReturn(memberCareerResponses);

    //when & then
    mockMvc.perform(post("/members/careers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(print())
        .andDo(document("add-career", REQUEST_FIELDS, RESPONSE_FIELDS));
  }

  @Test
  @DisplayName("내 명함에서 활동이력들을 성공적으로 삭제하면, 200 OK를 반환해줄 수 있다.")
  void test_deleteCareer() throws Exception {
    //given
    final List<Long> careerIds = List.of(1L, 2L);
    final MemberCareerDeleteRequest request = new MemberCareerDeleteRequest(careerIds);

    final List<MemberCareerResponse> memberCareerResponses = List.of(
        new MemberCareerResponse("동아리",
            List.of(
                new MemberActivityResponse(3L, "nexters")
            ))
    );

    when(memberCareerService.deleteCareer(any(), any()))
        .thenReturn(memberCareerResponses);

    //when & then
    mockMvc.perform(delete("/members/careers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("delete-career", REQUEST_FIELDS, RESPONSE_FIELDS));
  }

  @Test
  @DisplayName("내 활동들을 조회할 수 있다.")
  void test_findCareer() throws Exception {
    //given
    final List<MemberCareerResponse> memberCareerResponses = List.of(
        new MemberCareerResponse("동아리",
            List.of(
                new MemberActivityResponse(1L, "YAPP"),
                new MemberActivityResponse(2L, "DND"),
                new MemberActivityResponse(3L, "nexters")
            )),
        new MemberCareerResponse("컨퍼런스",
            List.of(
                new MemberActivityResponse(4L, "인프콘")
            )),
        new MemberCareerResponse("교육",
            List.of(
                new MemberActivityResponse(5L, "우아한테크코스")
            )),
        new MemberCareerResponse("직무",
            List.of(
                new MemberActivityResponse(6L, "Backend")
            ))
    );

    //when
    when(memberCareerService.findCareer(any()))
        .thenReturn(memberCareerResponses);

    //then
    mockMvc.perform(get("/members/careers")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("find-career", RESPONSE_FIELDS));
  }
}
