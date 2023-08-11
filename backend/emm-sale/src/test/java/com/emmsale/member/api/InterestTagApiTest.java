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
import com.emmsale.member.application.InterestTagService;
import com.emmsale.member.application.dto.InterestTagAddRequest;
import com.emmsale.member.application.dto.InterestTagDeleteRequest;
import com.emmsale.member.application.dto.InterestTagResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

@WebMvcTest(InterestTagApi.class)
class InterestTagApiTest extends MockMvcTestHelper {

  private static final ResponseFieldsSnippet INTEREST_TAG_RESPONSE_FIELDS = responseFields(
      fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("태그 ID"),
      fieldWithPath("[].name").type(JsonFieldType.STRING).description("태그명")
  );

  private static final RequestFieldsSnippet INTEREST_TAG_REQUEST_FIELDS = requestFields(
      fieldWithPath("tagIds").description("태그 id들"));

  @MockBean
  private InterestTagService interestTagService;

  @Test
  @DisplayName("행사 관심 태그들을 성공적으로 추가하면, 201 Created를 반환해줄 수 있다.")
  void addInterestTag() throws Exception {
    //given
    final String accessToken = "Bearer accessToken";
    final List<Long> tagIds = List.of(4L, 5L);
    final InterestTagAddRequest request = new InterestTagAddRequest(tagIds);

    final List<InterestTagResponse> interestTagResponse = List.of(
        new InterestTagResponse(1L, "백엔드"),
        new InterestTagResponse(2L, "프론트엔드"),
        new InterestTagResponse(4L, "IOS"),
        new InterestTagResponse(5L, "AI")
    );

    when(interestTagService.addInterestTag(any(), any()))
        .thenReturn(interestTagResponse);

    //when & then
    mockMvc.perform(post("/interest-tags")
            .header("Authorization", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(print())
        .andDo(document("add-interest-tag", INTEREST_TAG_REQUEST_FIELDS,
            INTEREST_TAG_RESPONSE_FIELDS));
  }

  @Test
  @DisplayName("행사 관심 태그들을 성공적으로 삭제하면, 200 OK를 반환해줄 수 있다.")
  void test_deleteInterestTag() throws Exception {
    //given
    final String accessToken = "Bearer accessToken";
    final List<Long> tagIds = List.of(1L);
    final InterestTagDeleteRequest request = new InterestTagDeleteRequest(tagIds);

    final List<InterestTagResponse> interestTagResponse = List.of(
        new InterestTagResponse(2L, "프론트엔드"),
        new InterestTagResponse(4L, "IOS"),
        new InterestTagResponse(5L, "AI")
    );

    when(interestTagService.deleteInterestTag(any(), any()))
        .thenReturn(interestTagResponse);

    //when & then
    mockMvc.perform(delete("/interest-tags")
            .header("Authorization", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("delete-interest-tag", INTEREST_TAG_REQUEST_FIELDS,
            INTEREST_TAG_RESPONSE_FIELDS));
  }

  @Test
  @DisplayName("내 행사 관심 태그들을 조회할 수 있다.")
  void test_findInterestTags() throws Exception {
    //given
    final List<InterestTagResponse> interestTagResponse = List.of(
        new InterestTagResponse(1L, "백엔드"),
        new InterestTagResponse(2L, "프론트엔드")
    );

    //when
    when(interestTagService.findInterestTags(any()))
        .thenReturn(interestTagResponse);

    //then
    mockMvc.perform(get("/interest-tags")
            .queryParam("member_id", "1")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("find-interest-tags", INTEREST_TAG_RESPONSE_FIELDS));
  }
}