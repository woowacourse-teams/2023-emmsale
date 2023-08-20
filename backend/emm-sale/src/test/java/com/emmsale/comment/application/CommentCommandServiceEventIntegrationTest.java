package com.emmsale.comment.application;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.emmsale.comment.application.dto.CommentAddRequest;
import com.emmsale.comment.domain.Comment;
import com.emmsale.comment.domain.CommentRepository;
import com.emmsale.event.EventFixture;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.notification.application.FirebaseCloudMessageClient;
import com.emmsale.notification.domain.UpdateNotification;
import com.emmsale.notification.domain.UpdateNotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class CommentCommandServiceEventIntegrationTest extends ServiceIntegrationTestHelper {

  @Autowired
  private CommentCommandService commentCommandService;
  @MockBean
  private FirebaseCloudMessageClient firebaseCloudMessageClient;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private CommentRepository commentRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private UpdateNotificationRepository updateNotificationRepository;

  @Test
  @DisplayName("publish(Comment) : 댓글 이벤트가 성공적으로 발행되면 UpdateNotification이 성공적으로 저장될 수 있다.")
  void test_publish_comment() throws Exception {
    //given
    final Event event = eventRepository.save(EventFixture.인프콘_2023());
    final Member 댓글_작성자1 = memberRepository.findById(1L).get();
    final Comment 부모_댓글 = commentRepository.save(Comment.createRoot(event, 댓글_작성자1, "내용1"));
    final Member 댓글_작성자2 = memberRepository.save(new Member(13444L, "image", "username"));

    doNothing().when(firebaseCloudMessageClient).sendMessageTo(any(UpdateNotification.class));

    final CommentAddRequest 알림_트리거_댓글_요청 =
        new CommentAddRequest("내용2", event.getId(), 부모_댓글.getId());

    //when
    commentCommandService.create(알림_트리거_댓글_요청, 댓글_작성자2);

    //then
    assertAll(
        () -> verify(firebaseCloudMessageClient, times(1)).sendMessageTo(any(UpdateNotification.class)),
        () -> assertEquals(1, updateNotificationRepository.findAll().size())
    );
  }

  @Test
  @DisplayName("publish(Comment) : 파이어베이스에 에러가 생겨도 UpdateNotification이 성공적으로 저장될 수 있다.")
  void test_publish_comment_error_firebase() throws Exception {
    //given
    final Event event = eventRepository.save(EventFixture.인프콘_2023());
    final Member 댓글_작성자1 = memberRepository.findById(1L).get();
    final Comment 부모_댓글 = commentRepository.save(Comment.createRoot(event, 댓글_작성자1, "내용1"));
    final Member 댓글_작성자2 = memberRepository.save(new Member(13444L, "image", "username"));

    doThrow(new IllegalArgumentException("파이어베이스 에러"))
        .when(firebaseCloudMessageClient).sendMessageTo(any(UpdateNotification.class));

    final CommentAddRequest 알림_트리거_댓글_요청 =
        new CommentAddRequest("내용2", event.getId(), 부모_댓글.getId());

    //when
    commentCommandService.create(알림_트리거_댓글_요청, 댓글_작성자2);

    //then
    assertAll(
        () -> verify(firebaseCloudMessageClient, times(1)).sendMessageTo(any(UpdateNotification.class)),
        () -> assertEquals(1, updateNotificationRepository.findAll().size())
    );
  }
}
