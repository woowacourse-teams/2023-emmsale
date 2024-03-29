package com.emmsale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.admin.tag.api.AdminTagApi;
import com.emmsale.member.domain.Member;
import com.emmsale.tag.api.TagApi;
import com.emmsale.tag.application.dto.TagRequest;
import com.emmsale.tag.application.dto.TagResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

@WebMvcTest({TagApi.class, AdminTagApi.class})
class TagApiTest extends MockMvcTestHelper {

  @Test
  @DisplayName("존재하는 태그를 전부 조회할 수 있다.")
  void findAll() throws Exception {
    //given
    final List<TagResponse> responses = List.of(
        new TagResponse(1L, "백엔드"),
        new TagResponse(2L, "안드로이드"),
        new TagResponse(3L, "프론트")
    );

    when(tagQueryService.findAll()).thenReturn(responses);

    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("태그 식별자"),
        fieldWithPath("[].name").type(JsonFieldType.STRING).description("태그 이름")
    );

    //when & then
    mockMvc.perform(get("/tags"))
        .andExpect(status().isOk())
        .andDo(document("find-tags", responseFields));
  }

  @Test
  @DisplayName("새로운 태그를 생성할 수 있다.")
  void addTag() throws Exception {
    //given
    final RequestFieldsSnippet requestFields = requestFields(
        fieldWithPath("name").type(JsonFieldType.STRING).description("태그 이름")
    );
    final TagRequest request = new TagRequest("프론트");
    final TagResponse response = new TagResponse(3L, "프론트");
    final String accessToken = "Bearer accessToken";

    when(tagCommandService.addTag(any(TagRequest.class), any(Member.class))).thenReturn(response);

    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("id").type(JsonFieldType.NUMBER).description("태그 식별자"),
        fieldWithPath("name").type(JsonFieldType.STRING).description("태그 이름")
    );

    //when & then
    mockMvc.perform(post("/admin/tags")
            .header("Authorization", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(document("add-tag", requestFields, responseFields));
  }
}
