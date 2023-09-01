package com.emmsale.tag.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.tag.application.dto.TagRequest;
import com.emmsale.tag.application.dto.TagResponse;
import com.emmsale.tag.domain.TagRepository;
import com.emmsale.tag.exception.TagException;
import com.emmsale.tag.exception.TagExceptionType;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TagCommandServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private TagCommandService commandService;
  @Autowired
  private TagRepository tagRepository;

  @Test
  @DisplayName("새로운 태그를 추가할 수 있다.")
  void findAll() {
    //given
    final String tagName = "프론트엔드";
    final TagRequest request = new TagRequest(tagName);
    final TagResponse expected = new TagResponse(1L, tagName);

    //when
    final TagResponse actual = commandService.addTag(request);

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

  @Test
  @DisplayName("이미 존재하는 태그를 추가하면 예외를 반환한다.")
  void addTag_duplicate_fail() {
    //given
    final String tagName = "프론트엔드";
    final TagRequest request = new TagRequest(tagName);
    commandService.addTag(request);

    //when
    final ThrowingCallable actual = () -> commandService.addTag(request);

    //then
    assertThatThrownBy(actual)
        .isInstanceOf(TagException.class)
        .hasMessage(TagExceptionType.ALEADY_EXIST_TAG.errorMessage());
  }


}
