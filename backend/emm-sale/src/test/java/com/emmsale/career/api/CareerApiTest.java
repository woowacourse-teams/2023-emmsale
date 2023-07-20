package com.emmsale.career.api;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.career.application.CareerService;
import com.emmsale.career.application.dto.ActivityResponse;
import com.emmsale.career.application.dto.CareerResponse;
import com.emmsale.helper.MockMvcTestHelper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

@WebMvcTest(CareerApi.class)
class CareerApiTest extends MockMvcTestHelper {

  @MockBean
  private CareerService careerService;

  @Test
  @DisplayName("커리어를 전제 조회할 수 있으면 200 OK를 반환한다.")
  void findAll() throws Exception {
    // given
    final ResponseFieldsSnippet responseFields = responseFields(
        fieldWithPath("[].activityName").type(JsonFieldType.STRING).description("career 분류"),
        fieldWithPath("[].activityResponses[].id").type(JsonFieldType.NUMBER)
            .description("career id"),
        fieldWithPath("[].activityResponses[].name").type(JsonFieldType.STRING)
            .description("career 이름")
    );

    final List<CareerResponse> careerResponses = List.of(
        new CareerResponse("동아리",
            List.of(
                new ActivityResponse(1L, "YAPP"),
                new ActivityResponse(2L, "DND"),
                new ActivityResponse(3L, "nexters")
            )),
        new CareerResponse("컨퍼런스",
            List.of(
                new ActivityResponse(4L, "인프콘")
            )),
        new CareerResponse("교육",
            List.of(
                new ActivityResponse(5L, "우아한테크코스")
            )),
        new CareerResponse("직무",
            List.of(
                new ActivityResponse(6L, "Backend")
            ))
    );

    when(careerService.findAll()).thenReturn(careerResponses);

    // when & then

    mockMvc.perform(get("/careers"))
        .andExpect(status().isOk())
        .andDo(document("findAll-career", responseFields));
  }
}
