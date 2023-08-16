package com.emmsale.block.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.block.application.BlockCommandService;
import com.emmsale.block.application.dto.BlockRequest;
import com.emmsale.helper.MockMvcTestHelper;
import com.emmsale.member.MemberFixture;
import com.emmsale.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;

@WebMvcTest(BlockApi.class)
class BlockApiTest extends MockMvcTestHelper {

  @MockBean
  private BlockCommandService blockCommandService;

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
  @DisplayName("성공적으로 차단을 해제한 경우 no content를 반환한다")
  void unregisterTest() throws Exception {
    final Long blockId = 2L;
    final String accessToken = "Bearer accessToken";

    final PathParametersSnippet pathParams = pathParameters(
        parameterWithName("block-id").description("차단 ID")
    );

    //when && then
    mockMvc.perform(delete("/blocks/{block-id}", blockId)
            .header("Authorization", accessToken))
        .andExpect(status().isNoContent())
        .andDo(document("unregister-block", pathParams));

    verify(blockCommandService, times(1)).unregister(any(), any());
  }
}
