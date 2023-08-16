package com.emmsale.block.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.block.application.BlockCommandService;
import com.emmsale.block.application.BlockQueryService;
import com.emmsale.block.application.dto.BlockRequest;
import com.emmsale.block.application.dto.BlockResponse;
import com.emmsale.helper.MockMvcTestHelper;
import com.emmsale.member.MemberFixture;
import com.emmsale.member.domain.Member;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

@WebMvcTest(BlockApi.class)
class BlockApiTest extends MockMvcTestHelper {

  @MockBean
  private BlockCommandService blockCommandService;
  @MockBean
  private BlockQueryService blockQueryService;

  @Test
  @DisplayName("성공적으로 사용자를 차단할 경우 201 CREATED를 반환한다.")
  void registerTest() throws Exception {
    //given
    final long blockMemberId = 2L;
    final Member requestMember = MemberFixture.memberFixture();
    final BlockRequest blockRequest = new BlockRequest(blockMemberId);
    final String accessToken = "Bearer accessToken";

    final RequestFieldsSnippet requestFields = requestFields(
        fieldWithPath("blockMemberId").description("차단할 사용자 id")
    );

    //when
    doNothing().when(blockCommandService).register(requestMember, blockRequest);

    //then
    mockMvc.perform(post("/blocks")
            .header("Authorization", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(blockRequest)))
        .andExpect(status().isCreated())
        .andDo(document("register-block", requestFields));
  }

  @Test
  @DisplayName("차단된 사용자들을 전부 조회한다.")
  void findAllTest() throws Exception {
    //given
    final String accessToken = "Bearer accessToken";
    final List<BlockResponse> responses = List.of(
        new BlockResponse(1L, 2L, "2번 멤버 이미지Url", "2번 멤버"),
        new BlockResponse(2L, 4L, "4번 멤버 이미지Url", "4번 멤버")
    );

    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("차단 id"),
        fieldWithPath("[].blockMemberId").type(JsonFieldType.NUMBER).description("차단한 사용자의 id"),
        fieldWithPath("[].imageUrl").type(JsonFieldType.STRING).description("차단한 사용자의 이미지 url"),
        fieldWithPath("[].memberName").type(JsonFieldType.STRING).description("차단한 사용자의 이름")
    );

    //when
    when(blockQueryService.findAll(any())).thenReturn(responses);

    //then
    mockMvc.perform(get("/blocks")
            .header("Authorization", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(document("find-blocks", responseFields));
  }
}