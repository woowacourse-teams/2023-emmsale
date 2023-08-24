package com.emmsale.activity.api;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.activity.application.dto.ActivityResponse;
import com.emmsale.activity.application.dto.ActivityResponses;
import com.emmsale.helper.MockMvcTestHelper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

@WebMvcTest(ActivityApi.class)
class ActivityApiTest extends MockMvcTestHelper {

  @Test
  @DisplayName("Activity를 전제 조회할 수 있으면 200 OK를 반환한다.")
  void findAll() throws Exception {
    // given
    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("[].activityType").type(JsonFieldType.STRING).description("activity 분류"),
        fieldWithPath("[].activityResponses[].id").type(JsonFieldType.NUMBER)
            .description("activity id"),
        fieldWithPath("[].activityResponses[].name").type(JsonFieldType.STRING)
            .description("activity 이름")
    );

    final List<ActivityResponses> activityResponses = List.of(
        new ActivityResponses("동아리",
            List.of(
                new ActivityResponse(1L, "YAPP"),
                new ActivityResponse(2L, "DND"),
                new ActivityResponse(3L, "nexters")
            )),
        new ActivityResponses("컨퍼런스",
            List.of(
                new ActivityResponse(4L, "인프콘")
            )),
        new ActivityResponses("교육",
            List.of(
                new ActivityResponse(5L, "우아한테크코스")
            )),
        new ActivityResponses("직무",
            List.of(
                new ActivityResponse(6L, "Backend")
            ))
    );

    when(activityService.findAll()).thenReturn(activityResponses);

    // when & then

    mockMvc.perform(get("/activities"))
        .andExpect(status().isOk())
        .andDo(document("find-all-activities", responseFields));
  }
}
