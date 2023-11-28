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
import com.emmsale.activity.domain.ActivityType;
import com.emmsale.admin.activity.api.AdminActivityApi;
import com.emmsale.member.domain.Member;
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

@WebMvcTest({ActivityApi.class, AdminActivityApi.class}) // TODO: 2023/11/18 Admin API Test 분리하기
class ActivityApiTest extends MockMvcTestHelper {

  @Test
  @DisplayName("Activity를 전제 조회할 수 있으면 200 OK를 반환한다.")
  void findAll() throws Exception {
    // given
    final ResponseFieldsSnippet responseFields = PayloadDocumentation.responseFields(
        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("activity id"),
        fieldWithPath("[].activityType").type(JsonFieldType.STRING).description("activity 분류"),
        fieldWithPath("[].name").type(JsonFieldType.STRING).description("activity 이름")
    );

    final List<ActivityResponse> expected = List.of(
        new ActivityResponse(1L, "동아리", "YAPP"),
        new ActivityResponse(2L, "동아리", "DND"),
        new ActivityResponse(3L, "동아리", "nexters"),
        new ActivityResponse(4L, "컨퍼런스", "인프콘"),
        new ActivityResponse(5L, "교육", "우아한테크코스"),
        new ActivityResponse(6L, "직무", "Backend")
    );

    Mockito.when(activityQueryService.findAll()).thenReturn(expected);

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
    final ActivityResponse response = new ActivityResponse(3L,
        ActivityType.CLUB.getValue(),
        "DND"
    );
    final String accessToken = "Bearer accessToken";

    when(activityCommandService.addActivity(any(ActivityAddRequest.class),
        any(Member.class))).thenReturn(response);

    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("id").type(JsonFieldType.NUMBER).description("활동 식별자"),
        fieldWithPath("activityType").type(JsonFieldType.STRING).description("활동 종류"),
        fieldWithPath("name").type(JsonFieldType.STRING).description("활동 이름")
    );

    //when & then
    mockMvc.perform(post("/admin/activities")
            .header("Authorization", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(document("add-activity", requestFields, responseFields));
  }
}
