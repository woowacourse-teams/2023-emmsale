package com.emmsale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.activity.api.ActivityApi;
import com.emmsale.activity.application.dto.ActivityAddRequest;
import com.emmsale.activity.application.dto.ActivityResponse;
import com.emmsale.activity.application.dto.ActivityResponses;
import com.emmsale.activity.domain.ActivityType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(ActivityApi.class)
class ActivityApiTest extends MockMvcTestHelper {

  @Test
  @DisplayName("Activity를 전제 조회할 수 있으면 200 OK를 반환한다.")
  void findAll() throws Exception {
    // given
    final ResponseFieldsSnippet responseFields = PayloadDocumentation.responseFields(
        PayloadDocumentation.fieldWithPath("[].activityType").type(JsonFieldType.STRING)
            .description("activity 분류"),
        PayloadDocumentation.fieldWithPath("[].activityResponses[].id").type(JsonFieldType.NUMBER)
            .description("activity id"),
        PayloadDocumentation.fieldWithPath("[].activityResponses[].name").type(JsonFieldType.STRING)
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

    Mockito.when(activityQueryService.findAll()).thenReturn(activityResponses);

    // when & then

    mockMvc.perform(MockMvcRequestBuilders.get("/activities"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("find-all-activities", responseFields));
  }

  @Test
  @DisplayName("새로운 활동을 생성할 수 있다.")
  void addTag() throws Exception {
    //given
    final RequestFieldsSnippet requestFields = requestFields(
        fieldWithPath("activityType").type(JsonFieldType.STRING).description("활동 유형"),
        fieldWithPath("name").type(JsonFieldType.STRING).description("활동 이름")
    );

    final ActivityAddRequest request = new ActivityAddRequest(ActivityType.CLUB, "DND");
    final ActivityResponse response = new ActivityResponse(3L, "DND");

    when(activityCommandService.addActivity(any())).thenReturn(response);

    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("id").type(JsonFieldType.NUMBER).description("활동 식별자"),
        fieldWithPath("name").type(JsonFieldType.STRING).description("활동 이름")
    );

    //when & then
    mockMvc.perform(post("/activities")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(document("add-activity", requestFields, responseFields));
  }
}
