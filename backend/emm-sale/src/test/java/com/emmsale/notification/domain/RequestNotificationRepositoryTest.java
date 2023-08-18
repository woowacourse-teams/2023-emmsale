package com.emmsale.notification.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Sql("/data-test.sql")
class RequestNotificationRepositoryTest {

  @Autowired
  private RequestNotificationRepository requestNotificationRepository;

  private RequestNotification 알림1;

  @BeforeEach
  void init() {
    알림1 = new RequestNotification(1L, 2L, 1L, "알림1");
    requestNotificationRepository.save(알림1);
  }

  @ParameterizedTest
  @CsvSource({
      "1,2,1,true",
      "1,2,2,false"
  })
  @DisplayName("existsBySenderIdAndReceiverIdAndEventId() : receiverId, senderId, EventId를 통해서 알림이 존재하는지 확인할 수 있다.")
  void test_existsBySenderIdAndReceiverIdAndEventId(
      final Long senderId, final Long receiverId, final Long eventId, final boolean result
  ) throws Exception {
    //when & then
    assertEquals(requestNotificationRepository.existsBySenderIdAndReceiverIdAndEventId(
        senderId, receiverId, eventId), result);
  }
}
