package com.emmsale.notification.application;

import static com.emmsale.notification.exception.NotificationExceptionType.*;
import static com.emmsale.notification.exception.NotificationExceptionType.CONVERTING_JSON_ERROR;
import static com.emmsale.notification.exception.NotificationExceptionType.NOT_FOUND_FCM_TOKEN;

import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.member.exception.MemberException;
import com.emmsale.member.exception.MemberExceptionType;
import com.emmsale.notification.application.dto.FcmMessage;
import com.emmsale.notification.application.dto.FcmMessage.Data;
import com.emmsale.notification.application.dto.FcmMessage.Message;
import com.emmsale.notification.domain.FcmToken;
import com.emmsale.notification.domain.FcmTokenRepository;
import com.emmsale.notification.domain.Notification;
import com.emmsale.notification.exception.NotificationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.List;
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
  private static final boolean DEFAULT_VALIDATE_ONLY = false;

  private final ObjectMapper objectMapper;
  private final MemberRepository memberRepository;
  private final RestTemplate restTemplate;
  private final FcmTokenRepository fcmTokenRepository;

  @Value("${firebase.project.id}")
  private String projectId;

  public void sendMessageTo(final Long receiverId, final Notification notification) {

    final FcmToken fcmToken = fcmTokenRepository.findByMemberId(receiverId)
        .orElseThrow(() -> new NotificationException(NOT_FOUND_FCM_TOKEN));

    final String message = makeMessage(fcmToken.getToken(), notification);

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
      log.error("firebase 접속 에러 = {}", exchange.getBody());
    }
  }

  private String makeMessage(final String targetToken, final Notification notification) {

    final Long senderId = notification.getSenderId();
    final Member sender = memberRepository.findById(senderId)
        .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));

    final Data messageData = new Data(
        sender.getName(), senderId.toString(),
        notification.getReceiverId().toString(), notification.getMessage(),
        sender.getOpenProfileUrl()
    );

    final Message message = new Message(messageData, targetToken);

    final FcmMessage fcmMessage = new FcmMessage(DEFAULT_VALIDATE_ONLY, message);

    try {
      return objectMapper.writeValueAsString(fcmMessage);
    } catch (JsonProcessingException e) {
      log.error("메세지 보낼 때 JSON 변환 에러", e);
      throw new NotificationException(CONVERTING_JSON_ERROR);
    }
  }

  private String getAccessToken() {
    final String firebaseConfigPath = FIREBASE_KEY_PATH;

    try {
      final GoogleCredentials googleCredentials = GoogleCredentials
          .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
          .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

      googleCredentials.refreshIfExpired();

      return googleCredentials.getAccessToken().getTokenValue();
    } catch (IOException e) {
      log.error("구글 토큰 요청 에러", e);
      throw new NotificationException(GOOGLE_REQUEST_TOKEN_ERROR);
    }
  }
}
