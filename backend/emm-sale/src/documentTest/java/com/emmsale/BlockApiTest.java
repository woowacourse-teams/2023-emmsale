package com.emmsale;

import com.emmsale.block.api.BlockApi;
import com.emmsale.block.application.dto.BlockRequest;
import com.emmsale.block.application.dto.BlockResponse;
import com.emmsale.member.MemberFixture;
import com.emmsale.member.domain.Member;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(BlockApi.class)
class BlockApiTest extends MockMvcTestHelper {


  @Test
  @DisplayName("성공적으로 사용자를 차단할 경우 201 CREATED를 반환한다.")
  void registerTest() throws Exception {
    //given
    final long blockMemberId = 2L;
    final Member requestMember = MemberFixture.memberFixture();
    final BlockRequest blockRequest = new BlockRequest(blockMemberId);
    final String accessToken = "Bearer accessToken";

    final RequestFieldsSnippet requestFields = PayloadDocumentation.requestFields(
        PayloadDocumentation.fieldWithPath("blockMemberId").description("차단할 사용자 id")
    );

    //when
    Mockito.doNothing().when(blockCommandService).register(requestMember, blockRequest);

    //then
    mockMvc.perform(RestDocumentationRequestBuilders.post("/blocks")
            .header("Authorization", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(blockRequest)))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andDo(MockMvcRestDocumentation.document("register-block", requestFields));
  }

  @Test
  @DisplayName("성공적으로 차단을 해제한 경우 no content를 반환한다")
  void unregisterTest() throws Exception {
    final Long blockId = 2L;
    final String accessToken = "Bearer accessToken";

    final PathParametersSnippet pathParams = RequestDocumentation.pathParameters(
        RequestDocumentation.parameterWithName("block-id").description("차단 ID")
    );

    //when && then
    mockMvc.perform(RestDocumentationRequestBuilders.delete("/blocks/{block-id}", blockId)
            .header("Authorization", accessToken))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andDo(MockMvcRestDocumentation.document("unregister-block", pathParams));

    Mockito.verify(blockCommandService, Mockito.times(1))
        .unregister(ArgumentMatchers.any(), ArgumentMatchers.any());
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

    final ResponseFieldsSnippet responseFields = PayloadDocumentation.responseFields(
        PayloadDocumentation.fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("차단 id"),
        PayloadDocumentation.fieldWithPath("[].blockMemberId").type(JsonFieldType.NUMBER)
            .description("차단한 사용자의 id"),
        PayloadDocumentation.fieldWithPath("[].imageUrl").type(JsonFieldType.STRING)
            .description("차단한 사용자의 이미지 url"),
        PayloadDocumentation.fieldWithPath("[].memberName").type(JsonFieldType.STRING)
            .description("차단한 사용자의 이름")
    );

    //when
    Mockito.when(blockQueryService.findAll(ArgumentMatchers.any())).thenReturn(responses);

    //then
    mockMvc.perform(RestDocumentationRequestBuilders.get("/blocks")
            .header("Authorization", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("find-blocks", responseFields));
  }
}
