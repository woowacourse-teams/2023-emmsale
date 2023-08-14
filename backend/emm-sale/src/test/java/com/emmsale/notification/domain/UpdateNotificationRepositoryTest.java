package com.emmsale.notification.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
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

  @Test
  @DisplayName("findAllByReceiverId() : 현재 접속해 있는 사용자가 받은 행사&댓글 알림들을 조회할 수 있다.")
  void test_findAllByReceiverId() throws Exception {
    //given
    final long receiverId = 1L;

    updateNotificationRepository.saveAll(List.of(
        new UpdateNotification(
            receiverId, 2L,
            UpdateNotificationType.COMMENT, LocalDateTime.now()
        ),
        new UpdateNotification(
            receiverId, 3L,
            UpdateNotificationType.EVENT, LocalDateTime.now()
        ),
        new UpdateNotification(
            2L, 4L,
            UpdateNotificationType.COMMENT, LocalDateTime.now()
        )
    ));

    final List<UpdateNotification> expected = List.of(
        new UpdateNotification(
            receiverId, 2L,
            UpdateNotificationType.COMMENT, LocalDateTime.now()
        ),
        new UpdateNotification(
            receiverId, 3L,
            UpdateNotificationType.EVENT, LocalDateTime.now()
        )
    );

    //when
    final List<UpdateNotification> actual =
        updateNotificationRepository.findAllByReceiverId(receiverId);

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .ignoringFields("createdAt", "id")
        .isEqualTo(expected);
  }
}
