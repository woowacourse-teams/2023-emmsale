package com.emmsale.member.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.activity.application.dto.ActivityResponse;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberActivityQueryServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private MemberActivityQueryService memberActivityQueryService;

  @Test
  @DisplayName("findActivities(): 유저의 Activity들을 조회한다.")
  void findActivities() {
    final Long memberId = 1L;

    final List<ActivityResponse> actual
        = memberActivityQueryService.findActivities(memberId);

    final List<ActivityResponse> expected = List.of(
        new ActivityResponse(1L, "동아리", "YAPP"),
        new ActivityResponse(2L, "동아리", "DND"),
        new ActivityResponse(3L, "동아리", "nexters")
    );

    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .isEqualTo(expected);
  }
}
