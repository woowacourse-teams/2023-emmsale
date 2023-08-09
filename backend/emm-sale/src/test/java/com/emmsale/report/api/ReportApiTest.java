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
import com.emmsale.report.application.dto.ReportRequest;
import com.emmsale.report.application.dto.ReportResponse;
import com.emmsale.report.domain.ReportReasonType;
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
    final RequestFieldsSnippet requestFields = requestFields(
        fieldWithPath("reporterId").type(JsonFieldType.NUMBER).description("신고자의 Id"),
        fieldWithPath("reportedId").type(JsonFieldType.NUMBER).description("신고 대상자의 Id(당하는 사람)"),
        fieldWithPath("content").type(JsonFieldType.STRING).description("신고 게시글의 내용"),
        fieldWithPath("reasonType").type(JsonFieldType.STRING).description(
            "신고 게시글의 신고 사유(ABUSE)"),
        fieldWithPath("type").type(JsonFieldType.STRING).description(
            "신고 게시글의 유형(COMMENT, PARTICIPANT, REQUEST_NOTIFICATION)")
    );

    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("id").type(JsonFieldType.NUMBER).description("신고 id"),
        fieldWithPath("reporterId").type(JsonFieldType.NUMBER).description("신고자의 Id"),
        fieldWithPath("reportedId").type(JsonFieldType.NUMBER).description("신고 대상자의 Id)"),
        fieldWithPath("content").type(JsonFieldType.STRING).description("신고 게시글의 내용"),
        fieldWithPath("reasonType").type(JsonFieldType.STRING)
            .description("신고 게시글의 신고 사유(ABUSE)"),
        fieldWithPath("type").type(JsonFieldType.STRING)
            .description("신고 게시글의 유형(COMMENT, PARTICIPANT, REQUEST_NOTIFICATION)"),
        fieldWithPath("createdAt").type(JsonFieldType.STRING)
            .description("신고 일자(yyyy:MM:dd:HH:mm:ss)")
    );
    final ReportRequest reportRequest = new ReportRequest(1L, 2L, "메롱메롱", ReportReasonType.ABUSE,
        ReportType.COMMENT);

    final ReportResponse reportResponse = new ReportResponse(1L, reportRequest.getReporterId(),
        reportRequest.getReportedId(),
        reportRequest.getContent(), reportRequest.getReasonType(), reportRequest.getType(),
        LocalDateTime.parse("2023-08-09T13:25:00"));

    when(reportCommandService.create(any())).thenReturn(reportResponse);

    // when & then
    mockMvc.perform(post("/reports").contentType(MediaType.APPLICATION_JSON_VALUE)
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
        fieldWithPath("[].content").type(JsonFieldType.STRING).description("신고 게시글의 내용"),
        fieldWithPath("[].reasonType").type(JsonFieldType.STRING)
            .description("신고 게시글의 신고 사유(ABUSE)"),
        fieldWithPath("[].type").type(JsonFieldType.STRING)
            .description("신고 게시글의 유형(COMMENT, PARTICIPANT, REQUEST_NOTIFICATION)"),
        fieldWithPath("[].createdAt").type(JsonFieldType.STRING)
            .description("신고 일자(yyyy:MM:dd:HH:mm:ss)")
    );

    final List<ReportResponse> reportResponses = List.of(
        new ReportResponse(1L, 1L, 2L, "메롱메롱", ReportReasonType.ABUSE, ReportType.COMMENT,
            LocalDateTime.parse("2023-08-09T13:25:00")),
        new ReportResponse(2L, 2L, 1L, "대충 심한 욕설", ReportReasonType.ABUSE, ReportType.PARTICIPANT,
            LocalDateTime.parse("2023-08-11T13:25:00")),
        new ReportResponse(3L, 1L, 3L, "사회적 물의를 일으킬 수 있는 발언", ReportReasonType.ABUSE,
            ReportType.REQUEST_NOTIFICATION,
            LocalDateTime.parse("2023-08-11T13:50:00")),
        new ReportResponse(4L, 4L, 1L, "도배글", ReportReasonType.ABUSE, ReportType.COMMENT,
            LocalDateTime.parse("2023-08-12T13:25:00"))

    );

    when(reportQueryService.findReports()).thenReturn(reportResponses);

    // when & then
    mockMvc.perform(get("/reports"))
        .andExpect(status().isOk())
        .andDo(document("find-reports", responseFields));


  }
}