package com.emmsale.report.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.helper.MockMvcTestHelper;
import com.emmsale.report.application.ReportCommandService;
import com.emmsale.report.application.ReportQueryService;
import com.emmsale.report.application.dto.ReportCreateRequest;
import com.emmsale.report.application.dto.ReportCreateResponse;
import com.emmsale.report.application.dto.ReportFindResponse;
import com.emmsale.report.domain.ReportType;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

@WebMvcTest(ReportApi.class)
class ReportApiTest extends MockMvcTestHelper {

  @MockBean
  private ReportCommandService reportCommandService;
  @MockBean
  private ReportQueryService reportQueryService;

  @Test
  @DisplayName("특정 게시글을 신고할 수 있다.")
  void addReport() throws Exception {
    // given
    final String accessToken = "Bearer access_token";
    final RequestFieldsSnippet requestFields = requestFields(
        fieldWithPath("reporterId").type(JsonFieldType.NUMBER).description("신고자의 Id"),
        fieldWithPath("reportedId").type(JsonFieldType.NUMBER).description("신고 대상자의 Id(당하는 사람)"),
        fieldWithPath("type").type(JsonFieldType.STRING).description(
            "신고 게시글의 유형(COMMENT, PARTICIPANT, REQUEST_NOTIFICATION)"),
        fieldWithPath("contentId").type(JsonFieldType.NUMBER).description("신고 게시물의 Id")
    );

    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("id").type(JsonFieldType.NUMBER).description("신고 id"),
        fieldWithPath("reporterId").type(JsonFieldType.NUMBER).description("신고자의 Id"),
        fieldWithPath("reportedId").type(JsonFieldType.NUMBER).description("신고 대상자의 Id)"),
        fieldWithPath("type").type(JsonFieldType.STRING)
            .description("신고 게시글의 유형(COMMENT, PARTICIPANT, REQUEST_NOTIFICATION)"),
        fieldWithPath("contentId").type(JsonFieldType.NUMBER).description("신고 게시물의 Id"),
        fieldWithPath("createdAt").type(JsonFieldType.STRING)
            .description("신고 일자(yyyy:MM:dd:HH:mm:ss)")
    );
    final ReportCreateRequest reportRequest = new ReportCreateRequest(1L, 2L, ReportType.COMMENT,
        1L);

    final ReportCreateResponse reportCreateResponse = new ReportCreateResponse(1L,
        reportRequest.getReporterId(),
        reportRequest.getReportedId(), reportRequest.getType(), reportRequest.getContentId(),
        LocalDateTime.parse("2023-08-09T13:25:00"));

    when(reportCommandService.create(any(), any())).thenReturn(reportCreateResponse);

    // when & then
    mockMvc.perform(post("/reports")
            .header("Authorization", accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(reportRequest)))
        .andExpect(status().isCreated())
        .andDo(document("add-report", requestFields, responseFields));


  }

  @Test
  @DisplayName("신고 목록을 조회할 수 있다.")
  void findReports() throws Exception {
    // given
    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("신고 id"),
        fieldWithPath("[].reporterId").type(JsonFieldType.NUMBER).description("신고자의 Id"),
        fieldWithPath("[].reportedId").type(JsonFieldType.NUMBER).description("신고 대상자의 Id)"),
        fieldWithPath("[].type").type(JsonFieldType.STRING)
            .description("신고 게시글의 유형(COMMENT, PARTICIPANT, REQUEST_NOTIFICATION)"),
        fieldWithPath("[].contentId").type(JsonFieldType.NUMBER).description("신고 게시물의 Id)"),
        fieldWithPath("[].createdAt").type(JsonFieldType.STRING)
            .description("신고 일자(yyyy:MM:dd:HH:mm:ss)")
    );

    final List<ReportFindResponse> reportFindResponse = List.of(
        new ReportFindResponse(1L, 1L, 2L, ReportType.COMMENT, 3L,
            LocalDateTime.parse("2023-08-09T13:25:00")),
        new ReportFindResponse(2L, 2L, 1L, ReportType.RECRUITMENT_POST, 1L,
            LocalDateTime.parse("2023-08-11T13:25:00")),
        new ReportFindResponse(3L, 1L, 3L, ReportType.REQUEST_NOTIFICATION, 5L,
            LocalDateTime.parse("2023-08-11T13:50:00")),
        new ReportFindResponse(4L, 4L, 1L, ReportType.COMMENT, 2L,
            LocalDateTime.parse("2023-08-12T13:25:00"))

    );

    when(reportQueryService.findReports()).thenReturn(reportFindResponse);

    // when & then
    mockMvc.perform(get("/reports"))
        .andExpect(status().isOk())
        .andDo(document("find-reports", responseFields));


  }
}