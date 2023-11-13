package com.emmsale.comment.application;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import com.emmsale.feed.domain.Feed;
import com.emmsale.feed.domain.repository.FeedRepository;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.notification.domain.Notification;
import com.emmsale.notification.domain.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CommentCommandServiceEventIntegrationTest extends ServiceIntegrationTestHelper {

  @Autowired
  private CommentCommandService commentCommandService;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private FeedRepository feedRepository;
  @Autowired
  private CommentRepository commentRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private NotificationRepository notificationRepository;
  private Member 댓글_작성자1;
  private Member 댓글_작성자2;
  private Feed feed;

  @BeforeEach
  void setUp() {
    final Event event = eventRepository.save(EventFixture.인프콘_2023());
    final Member 피드_작성자 = memberRepository.findById(2L).get();
    댓글_작성자1 = memberRepository.findById(1L).get();
    댓글_작성자2 = memberRepository.save(new Member(13444L, "image", "username"));
    feed = feedRepository.save(new Feed(event, 피드_작성자, "피드 제목", "피드 내용"));
  }

  @Test
  @DisplayName("publish(Comment) : 댓글 이벤트가 성공적으로 발행되면 UpdateNotification이 성공적으로 저장될 수 있다.")
  void test_publish_comment() throws Exception {
    //given
    final Comment 부모_댓글 = commentRepository.save(Comment.createRoot(feed, 댓글_작성자1, "내용1"));

    doNothing().when(firebaseCloudMessageClient).sendMessageTo(any(Notification.class), anyLong());

    final CommentAddRequest 알림_트리거_댓글_요청 =
        new CommentAddRequest("내용2", feed.getId(), 부모_댓글.getId());

    //when
    commentCommandService.create(알림_트리거_댓글_요청, 댓글_작성자2);

    //then
    assertAll(
        () -> verify(firebaseCloudMessageClient, times(1)).sendMessageTo(
            any(Notification.class), anyLong()),
        () -> assertEquals(1, notificationRepository.findAll().size())
    );
  }

  @Test
  @DisplayName("publish(Comment) : 파이어베이스에 에러가 생겨도 UpdateNotification이 성공적으로 저장될 수 있다.")
  void test_publish_comment_error_firebase() throws Exception {
    //given
    final Comment 부모_댓글 = commentRepository.save(Comment.createRoot(feed, 댓글_작성자1, "내용1"));

    doThrow(new IllegalArgumentException("파이어베이스 에러"))
        .when(firebaseCloudMessageClient).sendMessageTo(any(Notification.class), anyLong());

    final CommentAddRequest 알림_트리거_댓글_요청 =
        new CommentAddRequest("내용2", feed.getId(), 부모_댓글.getId());

    //when
    commentCommandService.create(알림_트리거_댓글_요청, 댓글_작성자2);

    //then
    assertAll(
        () -> verify(firebaseCloudMessageClient, times(1)).sendMessageTo(
            any(Notification.class), anyLong()),
        () -> assertEquals(1, notificationRepository.findAll().size())
    );
  }

  @Test
  @DisplayName("publish(Comment, Member) : 피드에 최상위 부모 댓글이 달리면 피드의 작성자에게 알림이 발송된다.")
  void test_publish_comment_to_feed_writer() {
    //given
    final CommentAddRequest 부모_댓글 = new CommentAddRequest("내용1", feed.getId(), null);

    doNothing().when(firebaseCloudMessageClient).sendMessageTo(any(Notification.class), anyLong());

    //when
    commentCommandService.create(부모_댓글, 댓글_작성자1);

    //then
    assertAll(
        () -> verify(firebaseCloudMessageClient, times(1))
            .sendMessageTo(any(Notification.class), anyLong()),
        () -> assertEquals(1, notificationRepository.findAll().size())
    );
  }

  @Test
  @DisplayName("publish(Comment, Member) : 피드에 최상위 부모 댓글 작성자와 피드 작성자가 동일할 경우 알림을 발행하지 않는다.")
  void test_do_not_publish_if_comment_writer_equals_feed_writer() {
    //given
    final CommentAddRequest 부모_댓글 = new CommentAddRequest("내용1", feed.getId(), null);

    doNothing().when(firebaseCloudMessageClient).sendMessageTo(any(Notification.class), anyLong());

    //when
    commentCommandService.create(부모_댓글, feed.getWriter());

    //then
    assertAll(
        () -> verify(firebaseCloudMessageClient, times(0))
            .sendMessageTo(any(Notification.class), anyLong()),
        () -> assertEquals(0, notificationRepository.findAll().size())
    );
  }
}
