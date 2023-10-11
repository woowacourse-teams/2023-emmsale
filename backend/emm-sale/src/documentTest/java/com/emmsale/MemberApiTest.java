package com.emmsale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emmsale.member.api.MemberApi;
import com.emmsale.member.application.dto.DescriptionRequest;
import com.emmsale.member.application.dto.MemberActivityAddRequest;
import com.emmsale.member.application.dto.MemberActivityInitialRequest;
import com.emmsale.member.application.dto.MemberActivityResponse;
import com.emmsale.member.application.dto.MemberActivityResponses;
import com.emmsale.member.application.dto.MemberImageResponse;
import com.emmsale.member.application.dto.MemberProfileResponse;
import com.emmsale.member.application.dto.OpenProfileUrlRequest;
import com.emmsale.member.domain.Member;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(MemberApi.class)
class MemberApiTest extends MockMvcTestHelper {

  private static final ResponseFieldsSnippet MEMBER_ACTIVITY_RESPONSE_FIELDS = responseFields(
      fieldWithPath("[].activityType").type(JsonFieldType.STRING).description("activity 분류"),
      fieldWithPath("[].memberActivityResponses[].id").type(JsonFieldType.NUMBER)
          .description("activity id"),
      fieldWithPath("[].memberActivityResponses[].name").type(JsonFieldType.STRING)
          .description("activity 이름")
  );
  private static final ResponseFieldsSnippet MEMBER_PROFILE_RESPONSE_FIELDS = responseFields(
      fieldWithPath("id").type(JsonFieldType.NUMBER).description("사용자 id"),
      fieldWithPath("name").type(JsonFieldType.STRING).description("사용자 이름"),
      fieldWithPath("description").type(JsonFieldType.STRING).description("사용자 한 줄 자기소개"),
      fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("사용자 프로필 이미지 url"),
      fieldWithPath("openProfileUrl").type(JsonFieldType.STRING).description("오픈 프로필 url"),
      fieldWithPath("githubUrl").type(JsonFieldType.STRING).description("깃허브 URL")
  );
  private static final RequestFieldsSnippet MEMBER_ACTIVITY_REQUEST_FIELDS = requestFields(
      fieldWithPath("activityIds").description("활동 id들"));
  private static final String FAKE_BEARER_ACCESS_TOKEN = "Bearer AccessToken";

  @Test
  @DisplayName("사용자 정보를 잘 저장하면, 204 no Content를 반환해줄 수 있다.")
  void register() throws Exception {
    //given
    final List<Long> activityIds = List.of(1L, 2L);
    final String name = "우르";

    final MemberActivityInitialRequest request = new MemberActivityInitialRequest(name,
        activityIds);

    final RequestFieldsSnippet requestFields = requestFields(
        fieldWithPath("activityIds").description("활동 id들"),
        fieldWithPath("name").description("사용자 이름")
    );

    //when & then
    mockMvc.perform(post("/members")
            .header(HttpHeaders.AUTHORIZATION, FAKE_BEARER_ACCESS_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent())
        .andDo(print())
        .andDo(document("initial-register-member", requestFields));
  }

  @Test
  @DisplayName("내 명함에서 활동이력들을 성공적으로 추가하면, 201 Created를 반환해줄 수 있다.")
  void addActivity() throws Exception {
    //given
    final List<Long> activityIds = List.of(4L, 5L, 6L);
    final MemberActivityAddRequest request = new MemberActivityAddRequest(activityIds);

    final List<MemberActivityResponses> memberActivityResponses = createMemberActivityResponses();

    when(memberActivityService.addActivity(any(), any()))
        .thenReturn(memberActivityResponses);

    //when & then
    mockMvc.perform(post("/members/activities")
            .header(HttpHeaders.AUTHORIZATION, FAKE_BEARER_ACCESS_TOKEN)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(print())
        .andDo(document("add-activity", MEMBER_ACTIVITY_REQUEST_FIELDS,
            MEMBER_ACTIVITY_RESPONSE_FIELDS));
  }

  private List<MemberActivityResponses> createMemberActivityResponses() {
    return List.of(
        new MemberActivityResponses("동아리",
            List.of(
                new MemberActivityResponse(1L, "YAPP"),
                new MemberActivityResponse(2L, "DND"),
                new MemberActivityResponse(3L, "nexters")
            )),
        new MemberActivityResponses("컨퍼런스",
            List.of(
                new MemberActivityResponse(4L, "인프콘")
            )),
        new MemberActivityResponses("교육",
            List.of(
                new MemberActivityResponse(5L, "우아한테크코스")
            )),
        new MemberActivityResponses("직무",
            List.of(
                new MemberActivityResponse(6L, "Backend")
            ))
    );
  }

  @Test
  @DisplayName("내 명함에서 활동이력들을 성공적으로 삭제하면, 200 OK를 반환해줄 수 있다.")
  void test_deleteActivity() throws Exception {
    //given
    final String activityIds = "1,2";

    final List<MemberActivityResponses> memberActivityResponses = List.of(
        new MemberActivityResponses("동아리",
            List.of(
                new MemberActivityResponse(3L, "nexters")
            ))
    );

    when(memberActivityService.deleteActivity(any(), any()))
        .thenReturn(memberActivityResponses);

    //when & then
    mockMvc.perform(
            delete("/members/activities?activity-ids={activityIds}", activityIds)
                .header(HttpHeaders.AUTHORIZATION, FAKE_BEARER_ACCESS_TOKEN))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("delete-activity",
            MEMBER_ACTIVITY_RESPONSE_FIELDS));
  }

  @Test
  @DisplayName("내 활동들을 조회할 수 있다.")
  void test_findActivity() throws Exception {
    //given
    final List<MemberActivityResponses> memberActivityResponse = createMemberActivityResponses();

    //when
    when(memberActivityService.findActivities(any()))
        .thenReturn(memberActivityResponse);

    //then
    mockMvc.perform(get("/members/1/activities")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("find-activity", MEMBER_ACTIVITY_RESPONSE_FIELDS));
  }

  @Test
  @DisplayName("사용자의 openProfileUrl을 성공적으로 업데이트하면, 200 OK가 반환된다.")
  void test_updateOpenProfileUrl() throws Exception {
    // given
    final String openProfileUrl = "https://open.kakao.com/o/openprofileurl";
    final OpenProfileUrlRequest request = new OpenProfileUrlRequest(openProfileUrl);

    final RequestFieldsSnippet REQUEST_FIELDS = requestFields(
        fieldWithPath("openProfileUrl").description("오픈 채팅 url")
    );

    // when
    final ResultActions result = mockMvc.perform(put("/members/open-profile-url")
        .header(HttpHeaders.AUTHORIZATION, FAKE_BEARER_ACCESS_TOKEN)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    // then
    result.andExpect(status().isNoContent())
        .andDo(print())
        .andDo(document("update-open-profile-url", REQUEST_FIELDS));
  }

  @Test
  @DisplayName("사용자의 description을 성공적으로 업데이트하면, 200 OK가 반환된다.")
  void test_updateDescription() throws Exception {
    // given
    final String description = "안녕하세요 김개발입니다.";
    final DescriptionRequest request = new DescriptionRequest(description);

    final RequestFieldsSnippet REQUEST_FIELDS = requestFields(
        fieldWithPath("description").description("한줄 자기소개(100자 이하여야 함)")
    );

    // when
    final ResultActions result = mockMvc.perform(put("/members/description")
        .header(HttpHeaders.AUTHORIZATION, FAKE_BEARER_ACCESS_TOKEN)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    // then
    result.andExpect(status().isNoContent())
        .andDo(print())
        .andDo(document("update-description", REQUEST_FIELDS));
  }

  @Test
  @DisplayName("특정 사용자의 프로필 정보를 조회할 수 있다.")
  void test_findProfile() throws Exception {
    //given
    final MemberProfileResponse memberProfileResponse = new MemberProfileResponse(
        1L,
        "김길동",
        "안녕하세요, 김길동입니다.",
        "https://image",
        "https://open.profile.url",
        "https://github.com/amaran-th"
    );
    when(memberQueryService.findProfile(any()))
        .thenReturn(memberProfileResponse);

    //when && then
    mockMvc.perform(get("/members/1")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("find-profile", MEMBER_PROFILE_RESPONSE_FIELDS));
  }

  @Test
  @DisplayName("회원탈퇴를 할 수 있다.")
  void deleteMemberTest() throws Exception {
    //given
    final long memberId = 1L;
    doNothing().when(memberUpdateService).deleteMember(any(), anyLong());
    final String accessToken = "access_token";

    //when
    mockMvc.perform(delete("/members/" + memberId)
            .header("Authorization", accessToken))
        .andExpect(status().isNoContent())
        .andDo(print())
        .andDo(document("delete-member"));
  }

  @Test
  @DisplayName("멤버 프로필을 변경할 수 있다.")
  void updateProfile() throws Exception {
    //given
    final MemberImageResponse memberImageResponse = new MemberImageResponse("http://imageUrl.png");
    final Long memberId = 1L;
    final String accessToken = "access_token";
    final MockMultipartHttpServletRequestBuilder builder = createUpdateProfileBuilder(memberId);

    when(memberUpdateService.updateMemberProfile
        (any(MultipartFile.class), anyLong(), any(Member.class)))
        .thenReturn(memberImageResponse);

    //when
    mockMvc.perform(builder
            .header("Authorization", accessToken))
        .andExpect(status().isOk())
        .andDo(print())
        .andDo(document("update-profile"));
  }

  private MockMultipartHttpServletRequestBuilder createUpdateProfileBuilder(final Long memberId)
      throws IOException {
    final MockMultipartFile image = new MockMultipartFile(
        "picture",
        "picture.jpg",
        MediaType.TEXT_PLAIN_VALUE,
        "test data".getBytes()
    );

    final MockMultipartHttpServletRequestBuilder builder =
        multipart("/members/{member-id}/profile", memberId)
            .file("image", image.getBytes());

    builder.with((mockHttpServletRequest) -> {
      mockHttpServletRequest.setMethod("PATCH");
      return mockHttpServletRequest;
    });

    return builder;
  }
}
