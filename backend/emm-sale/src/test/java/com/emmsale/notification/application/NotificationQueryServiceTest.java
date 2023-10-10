package com.emmsale.notification.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.notification.NotificationFixture;
import com.emmsale.notification.application.dto.NotificationDetailResponse;
import com.emmsale.notification.domain.Notification;
import com.emmsale.notification.domain.NotificationRepository;
import com.emmsale.notification.domain.NotificationType;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class NotificationQueryServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private NotificationQueryService notificationQueryService;
  @Autowired
  private NotificationRepository notificationRepository;
  @Autowired
  private MemberRepository memberRepository;

  @Test
  @DisplayName("findAllByMemberId() : 해당 사용자가 받은 알림들을 모두 조회할 수 있다.")
  void test_findAllByMemberId() throws Exception {
    //given
    final Member loginMember = memberRepository.findById(1L).get();

    final Notification notification1 = notificationRepository.save(
        new Notification(
            NotificationType.COMMENT, loginMember.getId(),
            3333L, LocalDateTime.now(),
            NotificationFixture.commentJsonData()
        )
    );

    final Notification notification2 = notificationRepository.save(
        new Notification(
            NotificationType.EVENT, loginMember.getId(),
            3333L, LocalDateTime.now(),
            NotificationFixture.eventJsonData()
        )
    );

    notificationRepository.save(
        new Notification(
            NotificationType.COMMENT, 3223L,
            3333L, LocalDateTime.now(),
            NotificationFixture.commentJsonData()
        )
    );

    final List<NotificationDetailResponse> expect = List.of(
        NotificationDetailResponse.from(notification1),
        NotificationDetailResponse.from(notification2)
    );

    //when
    final List<NotificationDetailResponse> actual = notificationQueryService.findAllByMemberId(
        loginMember, loginMember.getId()
    );

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);
  }
}
