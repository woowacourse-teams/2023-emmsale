package com.emmsale.notification.application;

import static com.emmsale.notification.exception.NotificationExceptionType.FIREBASE_CONNECT_ERROR;
import static com.emmsale.notification.exception.NotificationExceptionType.GOOGLE_REQUEST_TOKEN_ERROR;
import static com.emmsale.notification.exception.NotificationExceptionType.NOT_FOUND_FCM_TOKEN;

import com.emmsale.event_publisher.MessageNotificationEvent;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.notification.application.generator.CommentNotificationMessageGenerator;
import com.emmsale.notification.application.generator.EventNotificationMessageGenerator;
import com.emmsale.notification.application.generator.MessageNotificationMessageGenerator;
import com.emmsale.notification.application.generator.NotificationMessageGenerator;
import com.emmsale.notification.application.generator.UpdateNotificationMessageGenerator;
import com.emmsale.notification.domain.FcmToken;
import com.emmsale.notification.domain.FcmTokenRepository;
import com.emmsale.notification.domain.Notification;
import com.emmsale.notification.domain.NotificationType;
import com.emmsale.notification.domain.UpdateNotification;
import com.emmsale.notification.exception.NotificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirebaseCloudMessageClient {

  private static final String PREFIX_ACCESS_TOKEN = "Bearer ";
  private static final String PREFIX_FCM_REQUEST_URL = "https://fcm.googleapis.com/v1/projects/";
  private static final String POSTFIX_FCM_REQUEST_URL = "/messages:send";
  private static final String FIREBASE_KEY_PATH = "kerdy-submodule/firebase-kerdy.json";
  private static final String GOOGLE_AUTH_URL = "https://www.googleapis.com/auth/cloud-platform";
  private static final Map<NotificationType, Function<Notification, NotificationMessageGenerator>> GENERATOR_MAP =
      Map.of(
          NotificationType.EVENT, EventNotificationMessageGenerator::new,
          NotificationType.COMMENT, CommentNotificationMessageGenerator::new
      );


  private final ObjectMapper objectMapper;
  private final MemberRepository memberRepository;
  private final RestTemplate restTemplate;
  private final FcmTokenRepository fcmTokenRepository;

  @Value("${firebase.project.id}")
  private String projectId;

  public void sendMessageTo(final UpdateNotification updateNotification) {
    sendMessageTo(
        updateNotification.getReceiverId(),
        new UpdateNotificationMessageGenerator(updateNotification)
    );
  }

  public void sendMessageTo(final MessageNotificationEvent messageNotificationEvent) {
    sendMessageTo(
        messageNotificationEvent.getReceiverId(),
        new MessageNotificationMessageGenerator(messageNotificationEvent)
    );
  }

  public void sendMessageTo(final Notification notification, final Long receiverId) {
    sendMessageTo(
        receiverId,
        GENERATOR_MAP.get(notification.getType()).apply(notification)
    );
  }

  private void sendMessageTo(
      final Long receiverId,
      final NotificationMessageGenerator messageGenerator
  ) {
    final FcmToken fcmToken = fcmTokenRepository.findByMemberId(receiverId)
        .orElseThrow(() -> new NotificationException(NOT_FOUND_FCM_TOKEN));

    final String message = messageGenerator.makeMessage(
        fcmToken.getToken(),
        objectMapper,
        memberRepository
    );

    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    httpHeaders.add(HttpHeaders.AUTHORIZATION, PREFIX_ACCESS_TOKEN + getAccessToken());

    final HttpEntity<String> httpEntity = new HttpEntity<>(message, httpHeaders);

    final String fcmRequestUrl = PREFIX_FCM_REQUEST_URL + projectId + POSTFIX_FCM_REQUEST_URL;

    final ResponseEntity<String> exchange = restTemplate.exchange(
        fcmRequestUrl,
        HttpMethod.POST,
        httpEntity,
        String.class
    );

    if (exchange.getStatusCode().isError()) {
      throw new NotificationException(FIREBASE_CONNECT_ERROR);
    }
  }

  private String getAccessToken() {
    try {
      final GoogleCredentials googleCredentials = GoogleCredentials
          .fromStream(new ClassPathResource(FIREBASE_KEY_PATH).getInputStream())
          .createScoped(List.of(GOOGLE_AUTH_URL));

      googleCredentials.refreshIfExpired();

      return googleCredentials.getAccessToken().getTokenValue();
    } catch (IOException e) {
      throw new NotificationException(GOOGLE_REQUEST_TOKEN_ERROR);
    }
  }
}
