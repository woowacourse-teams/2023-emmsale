package com.emmsale.notification.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.helper.JpaRepositorySliceTestHelper;
import com.emmsale.notification.NotificationFixture;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class NotificationRepositoryTest extends JpaRepositorySliceTestHelper {

  @Autowired
  private NotificationRepository notificationRepository;

  private Notification notification1, notification2, notification3;

  @BeforeEach
  void setUp() {
    notification1 = notificationRepository.save(
        new Notification(NotificationType.COMMENT, 26L, 3333L, LocalDateTime.now(),
            NotificationFixture.commentJsonData())
    );

    notification2 = notificationRepository.save(
        new Notification(NotificationType.EVENT, 26L, 3333L, LocalDateTime.now(),
            NotificationFixture.eventJsonData())
    );

    notification3 = notificationRepository.save(
        new Notification(NotificationType.COMMENT, 3332L, 3333L, LocalDateTime.now(),
            NotificationFixture.commentJsonData())
    );
  }

  @Test
  @DisplayName("findAllByReceiverId() : 특정 사용자가 받은 알림 목록들을 조회할 수 있다.")
  void test_findAllByReceiverId() throws Exception {
    //given
    final List<Notification> expect = List.of(notification1, notification2);
    final long receiverId = 26L;

    //when
    final List<Notification> actual = notificationRepository.findAllByReceiverId(receiverId);

    //then
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);
  }

  @Test
  @DisplayName("findAllByIdIn() : 해당 id들을 가진 Notification들을 조회할 수 있다.")
  void test_findAllByIdsIn() throws Exception {
    //given
    final List<Notification> expected = List.of(notification1, notification2);

    final List<Long> notificationIds = List.of(notification1.getId(), notification2.getId());

    //when
    final List<Notification> actual =
        notificationRepository.findAllByIdIn(notificationIds);

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .ignoringFields("createdAt")
        .isEqualTo(expected);
  }

  @Test
  @DisplayName("deleteBatchByIdsIn() : IN 쿼리를 통해 알림들을 배치 삭제할 수 있다.")
  void test_deleteBatchByIdsIn() throws Exception {
    //given
    //존재하지 않는 알림 ID
    final List<Long> nonExistedNotificationIds = List.of(3L, 4L, 5L);
    final List<Long> existedNotificationIds = List.of(notification1.getId(), notification2.getId());
    final List<Long> deleteIds = new ArrayList<>();
    deleteIds.addAll(nonExistedNotificationIds);
    deleteIds.addAll(existedNotificationIds);

    final List<Long> actual = Collections.emptyList();

    //when
    notificationRepository.deleteBatchByIdsIn(deleteIds);

    //then
    final List<Notification> expected = notificationRepository.findAllByIdIn(deleteIds);

    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .ignoringFields("createdAt")
        .isEqualTo(expected);
  }
}
