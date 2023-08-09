package com.emmsale.notification.application;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.emmsale.comment.domain.Comment;
import com.emmsale.comment.event.UpdateNotificationEvent;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.notification.domain.UpdateNotification;
import com.emmsale.notification.domain.UpdateNotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UpdateNotificationCommandServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private UpdateNotificationCommandService updateNotificationCommandService;
  @Autowired
  private UpdateNotificationRepository updateNotificationRepository;
  private FirebaseCloudMessageClient mockingFirebaseCloudMessageClient;

  @BeforeEach
  void setUp() {
    mockingFirebaseCloudMessageClient = mock(FirebaseCloudMessageClient.class);

    updateNotificationCommandService = new UpdateNotificationCommandService(
        updateNotificationRepository, mockingFirebaseCloudMessageClient
    );
  }

  @Test
  @DisplayName("createUpdateNotification() : UpdateNotificationEvent 이벤트가 발생했을 때 해당 메서드가 호출된다.")
  void test_createUpdateNotification() throws Exception {
    //given
    final UpdateNotificationEvent event = new UpdateNotificationEvent(
        1L,
        2L,
        Comment.class.getName()
    );

    //when
    updateNotificationCommandService.createUpdateNotification(event);

    //then
    assertAll(
        () -> assertEquals(1, updateNotificationRepository.findAll().size()),
        () -> verify(mockingFirebaseCloudMessageClient, times(1))
            .sendMessageTo((UpdateNotification) any())
    );
  }
}
