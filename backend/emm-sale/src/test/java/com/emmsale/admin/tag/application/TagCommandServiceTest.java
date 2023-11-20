package com.emmsale.admin.tag.application;

import static com.emmsale.member.MemberFixture.adminMember;
import static com.emmsale.member.MemberFixture.generalMember;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.login.exception.LoginException;
import com.emmsale.login.exception.LoginExceptionType;
import com.emmsale.tag.application.dto.TagRequest;
import com.emmsale.tag.application.dto.TagResponse;
import com.emmsale.tag.exception.TagException;
import com.emmsale.tag.exception.TagExceptionType;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TagCommandServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private TagCommandService commandService;

  @Test
  @DisplayName("새로운 태그를 추가할 수 있다.")
  void findAll() {
    //given
    final String tagName = "프론트엔드";
    final TagRequest request = new TagRequest(tagName);
    final TagResponse expected = new TagResponse(1L, tagName);

    //when
    final TagResponse actual = commandService.addTag(request, adminMember());

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

  @Test
  @DisplayName("이미 존재하는 태그를 추가하면 예외를 반환한다.")
  void addTag_fail_duplicate() {
    //given
    final String tagName = "프론트엔드";
    final TagRequest request = new TagRequest(tagName);
    commandService.addTag(request, adminMember());

    //when
    final ThrowingCallable actual = () -> commandService.addTag(request, adminMember());

    //then
    assertThatThrownBy(actual)
        .isInstanceOf(TagException.class)
        .hasMessage(TagExceptionType.ALEADY_EXIST_TAG.errorMessage());
  }

  @Test
  @DisplayName("관리자가 아닌 회원이 태그를 추가하면 예외를 반환한다.")
  void addTag_fail_authorization() {
    //given
    final String tagName = "프론트엔드";
    final TagRequest request = new TagRequest(tagName);

    //when
    final ThrowingCallable actual = () -> commandService.addTag(request, generalMember());

    //then
    assertThatThrownBy(actual)
        .isInstanceOf(LoginException.class)
        .hasMessage(LoginExceptionType.INVALID_ACCESS_TOKEN.errorMessage());
  }
}
