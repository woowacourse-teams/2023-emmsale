package com.emmsale.notification.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Sql("/data-test.sql")
class UpdateNotificationRepositoryTest {

  @Autowired
  private UpdateNotificationRepository updateNotificationRepository;

  private UpdateNotification notification1;
  private UpdateNotification notification2;
  private UpdateNotification notification3;
  private Long receiverId;

  @BeforeEach
  void init() {
    receiverId = 1L;

    notification1 = new UpdateNotification(
        receiverId, 2L,
        UpdateNotificationType.COMMENT, LocalDateTime.now()
    );
    notification2 = new UpdateNotification(
        receiverId, 3L,
        UpdateNotificationType.EVENT, LocalDateTime.now()
    );
    notification3 = new UpdateNotification(
        2L, 4L,
        UpdateNotificationType.COMMENT, LocalDateTime.now()
    );

    updateNotificationRepository.saveAll(List.of(notification1, notification2, notification3));
  }

  @Test
  @DisplayName("findAllByReceiverId() : 현재 접속해 있는 사용자가 받은 행사&댓글 알림들을 조회할 수 있다.")
  void test_findAllByReceiverId() throws Exception {
    //given
    final List<UpdateNotification> expected = List.of(notification1, notification2);

    //when
    final List<UpdateNotification> actual =
        updateNotificationRepository.findAllByReceiverId(receiverId);

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .ignoringFields("createdAt")
        .isEqualTo(expected);
  }

  @Test
  @DisplayName("findAllByIdIn() : 해당 id들을 가진 Notification들을 조회할 수 있다.")
  void test_findAllByIdsIn() throws Exception {
    //given
    final List<UpdateNotification> expected = List.of(notification1, notification2);

    final List<Long> notificationIds = List.of(notification1.getId(), notification2.getId());

    //when
    final List<UpdateNotification> actual =
        updateNotificationRepository.findAllByIdIn(notificationIds);

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
    updateNotificationRepository.deleteBatchByIdsIn(deleteIds);

    //then
    final List<UpdateNotification> expected =
        updateNotificationRepository.findAllByIdIn(deleteIds);

    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .ignoringFields("createdAt")
        .isEqualTo(expected);
  }
}
