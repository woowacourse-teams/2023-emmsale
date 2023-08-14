package com.emmsale.tag.api;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.helper.MockMvcTestHelper;
import com.emmsale.tag.application.TagQueryService;
import com.emmsale.tag.application.dto.TagResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

@WebMvcTest(TagApi.class)
class TagApiTest extends MockMvcTestHelper {

  @MockBean
  private TagQueryService queryService;

  @Test
  @DisplayName("존재하는 태그를 전부 조회할 수 있다.")
  void findAll() throws Exception {
    //given
    final List<TagResponse> responses = List.of(new TagResponse(1L, "백엔드"),
        new TagResponse(2L, "안드로이드"), new TagResponse(3L, "프론트"));
    when(queryService.findAll()).thenReturn(responses);

    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("태그 식별자"),
        fieldWithPath("[].name").type(JsonFieldType.STRING).description("태그 이름")
    );

    //when & then
    mockMvc.perform(get("/tags"))
        .andExpect(status().isOk())
        .andDo(document("find-tags", responseFields));
  }
}
