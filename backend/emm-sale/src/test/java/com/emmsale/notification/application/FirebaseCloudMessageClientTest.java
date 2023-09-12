package com.emmsale.notification.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.emmsale.event_publisher.MessageNotificationEvent;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.message_room.domain.Room;
import com.emmsale.message_room.domain.RoomId;
import com.emmsale.message_room.domain.RoomRepository;
import com.emmsale.notification.domain.FcmToken;
import com.emmsale.notification.domain.FcmTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class FirebaseCloudMessageClientTest extends ServiceIntegrationTestHelper {

  @Autowired
  private FcmTokenRepository fcmTokenRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private RoomRepository roomRepository;
  @Autowired
  private ObjectMapper objectMapper;

  private RestTemplate restTemplate;
  private FirebaseCloudMessageClient firebaseCloudMessageClient;

  @BeforeEach
  void setUp() {
    restTemplate = mock(RestTemplate.class);
    firebaseCloudMessageClient
        = new FirebaseCloudMessageClient(objectMapper, memberRepository,
        restTemplate, fcmTokenRepository);
  }

  @Test
  @DisplayName("sendMessageTo() : Message와 receiverId를 받아, 메시지 알림을 보낸다.")
  void sendMessageToMessageNotification() {
    //given
    final Member sender
        = memberRepository.save(new Member(123L, "member1 image", "sender"));
    final Member receiver
        = memberRepository.save(new Member(124L, "member2 image", "receiver"));
    fcmTokenRepository.save(new FcmToken("fcm-token", receiver.getId()));

    final String uuid = "uuid-str";
    roomRepository.save(
        new Room(new RoomId(uuid, sender.getId()), LocalDateTime.now()));
    roomRepository.save(
        new Room(new RoomId(uuid, receiver.getId()), LocalDateTime.now()));

    final LocalDateTime messageSendTime = LocalDateTime.now().plusDays(10);
    final MessageNotificationEvent messageNotificationEvent = new MessageNotificationEvent(
        uuid, "message", sender.getId(), receiver.getId(), messageSendTime
    );
    final ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.OK);

    when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
        eq(String.class)))
        .thenReturn(responseEntity);

    //when
    firebaseCloudMessageClient.sendMessageTo(messageNotificationEvent);

    //then
    verify(restTemplate, times(1))
        .exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
  }
}
