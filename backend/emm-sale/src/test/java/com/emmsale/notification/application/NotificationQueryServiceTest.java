package com.emmsale.notification.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.notification.application.dto.NotificationAllResponse;
import com.emmsale.notification.domain.Notification;
import com.emmsale.notification.domain.NotificationRepository;
import com.emmsale.notification.domain.NotificationType;
import java.util.List;
import org.assertj.core.api.Assertions;
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

  private String commentJsonData1, commentJsonData2, eventJsonData1;

  @BeforeEach
  void setUp() {
    commentJsonData1 = "{"
        + "\"receiverId\":1,"
        + "\"redirectId\":5100,"
        + "\"createdAt\":\"2023-09-27T15:41:59.802027\","
        + "\"notificationType\":\"COMMENT\","
        + "\"content\":\"content\","
        + "\"writer\":\"writer\","
        + "\"writerImageUrl\":\"imageUrl\""
        + "}";

    eventJsonData1 = "{"
        + "\"receiverId\":1,"
        + "\"redirectId\":5101,"
        + "\"notificationType\":\"EVENT\","
        + "\"createdAt\":\"2023-09-27T15:41:59.802027\","
        + "\"title\":\"title\""
        + "}";

    commentJsonData2 = "{"
        + "\"receiverId\":2,"
        + "\"redirectId\":5102,"
        + "\"createdAt\":\"2023-09-27T15:41:59.802027\","
        + "\"notificationType\":\"COMMENT\","
        + "\"content\":\"content\","
        + "\"writer\":\"writer\","
        + "\"writerImageUrl\":\"imageUrl\""
        + "}";
  }

  @Test
  @DisplayName("findAllByMemberId() : 해당 사용자가 받은 알림들을 모두 조회할 수 있다.")
  void test_findAllByMemberId() throws Exception {
    //given
    final Member loginMember = memberRepository.findById(1L).get();

    final Notification notification1 = notificationRepository.save(
        new Notification(NotificationType.COMMENT, commentJsonData1)
    );

    final Notification notification2 = notificationRepository.save(
        new Notification(NotificationType.EVENT, eventJsonData1)
    );

    notificationRepository.save(new Notification(NotificationType.COMMENT, commentJsonData2));

    final List<NotificationAllResponse> expect = List.of(
        NotificationAllResponse.from(notification1),
        NotificationAllResponse.from(notification2)
    );

    //when
    final List<NotificationAllResponse> actual = notificationQueryService.findAllByMemberId(
        loginMember, loginMember.getId()
    );

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);
  }
}
