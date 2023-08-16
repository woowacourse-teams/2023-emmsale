package com.emmsale.tag.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.tag.application.dto.TagResponse;
import com.emmsale.tag.domain.Tag;
import com.emmsale.tag.domain.TagRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TagQueryServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private TagQueryService queryService;
  @Autowired
  private TagRepository tagRepository;

  @Test
  @DisplayName("존재하는 태그를 전부 조회할 수 있다.")
  void findAll() {
    //given
    final Tag 백엔드 = tagRepository.save(new Tag("백엔드"));
    final Tag 안드로이드 = tagRepository.save(new Tag("안드로이드"));
    final Tag 프론트엔드 = tagRepository.save(new Tag("프론트엔드"));
    final List<TagResponse> expected = List.of(
        TagResponse.from(백엔드),
        TagResponse.from(안드로이드),
        TagResponse.from(프론트엔드)
    );

    //when
    final List<TagResponse> actual = queryService.findAll();

    //then
    assertThat(actual)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrderElementsOf(expected);
  }
}
