package com.emmsale.comment.application;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.emmsale.comment.application.dto.CommentAddRequest;
import com.emmsale.comment.application.dto.CommentModifyRequest;
import com.emmsale.comment.application.dto.CommentResponse;
import com.emmsale.comment.domain.Comment;
import com.emmsale.comment.domain.CommentRepository;
import com.emmsale.comment.event.UpdateNotificationEvent;
import com.emmsale.comment.exception.CommentException;
import com.emmsale.comment.exception.CommentExceptionType;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventType;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event_publisher.EventPublisher;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class CommentCommandServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private CommentCommandService commentCommandService;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private CommentRepository commentRepository;
  @MockBean
  private EventPublisher eventPublisher;

  private Event event;
  private Member 댓글_작성자;

  @BeforeEach
  void init() {
    final LocalDateTime beforeDateTime = LocalDateTime.now();
    final LocalDateTime afterDateTime = beforeDateTime.plusDays(1);
    event = eventRepository.save(
        new Event(
            "event",
            "location",
            beforeDateTime,
            afterDateTime,
            beforeDateTime,
            afterDateTime,
            "url",
            EventType.CONFERENCE,
            "https://image.com"
        )
    );
    댓글_작성자 = memberRepository.findById(1L).get();
  }

  @Test
  @DisplayName("create() : 댓글 부모 id가 없으면 Root(최상단) 댓글을 생성할 수 있다.")
  void test_create() throws Exception {
    //given
    final String content = "내용";

    final CommentAddRequest 부모_댓글_요청 = new CommentAddRequest(content, event.getId(), null);

    doNothing().when(eventPublisher).publish(any(UpdateNotificationEvent.class));

    //when
    final CommentResponse 부모_댓글_응답 = commentCommandService.create(부모_댓글_요청, 댓글_작성자);

    //then
    assertAll(
        () -> assertEquals(부모_댓글_응답.getContent(), content),
        () -> assertNotNull(부모_댓글_응답.getCommentId()),
        () -> assertNull(부모_댓글_응답.getParentId())
    );
  }

  @Test
  @DisplayName("create() : 자신이 작성한 댓글이 Root 댓글이라면 알림이 가지 않는다.")
  void test_create_root_notification() throws Exception {
    //given
    final String content = "내용";
    final CommentAddRequest 부모_댓글_요청 = new CommentAddRequest(content, event.getId(), null);

    doNothing().when(eventPublisher).publish(any(UpdateNotificationEvent.class));

    //when
    commentCommandService.create(부모_댓글_요청, 댓글_작성자);

    //then
    verify(eventPublisher, times(0)).publish(any(UpdateNotificationEvent.class));
  }

  @Test
  @DisplayName("create() : 댓글 부모 id가 있으면 그 자식 댓글(대댓글)을 생성할 수 있다.")
  void test_create_child() throws Exception {
    //given
    final String content = "내용";
    final Long eventId = event.getId();

    final CommentAddRequest 부모_댓글_요청 = new CommentAddRequest(content, eventId, null);
    final CommentResponse 부모_댓글_응답 = commentCommandService.create(부모_댓글_요청, 댓글_작성자);
    final CommentAddRequest 자식_댓글_요청 = new CommentAddRequest(content, eventId, 1L);

    doNothing().when(eventPublisher).publish(any(UpdateNotificationEvent.class));

    //when
    final CommentResponse 자식_댓글_응답 = commentCommandService.create(자식_댓글_요청, 댓글_작성자);

    //then
    assertAll(
        () -> assertEquals(자식_댓글_응답.getContent(), content),
        () -> assertNotNull(자식_댓글_응답.getCommentId()),
        () -> assertEquals(부모_댓글_응답.getCommentId(), 자식_댓글_응답.getParentId())
    );
  }

  @Test
  @DisplayName("create() : 대댓글을 생성할 경우 부모, 자식 댓글에서 나의 댓글만 존재한다면 알림이 가지 않는다.")
  void test_create_child_not_notification() throws Exception {
    //given
    final String content = "내용";
    final Long eventId = event.getId();

    final CommentAddRequest 부모_댓글_요청 = new CommentAddRequest(content, eventId, null);
    commentCommandService.create(부모_댓글_요청, 댓글_작성자);
    final CommentAddRequest 자식_댓글_요청 = new CommentAddRequest(content, eventId, 1L);

    doNothing().when(eventPublisher).publish(any(UpdateNotificationEvent.class));

    //when
    commentCommandService.create(자식_댓글_요청, 댓글_작성자);

    //then
    verify(eventPublisher, times(0)).publish(any(UpdateNotificationEvent.class));
  }

  /**
   * 1 ㄴ2  (1에게 알림) ㄴ3  (1,2에게 알림)
   * <p>
   * <p>
   * 새로운 대댓글 ㄴ2  (1,3에게 알림)
   */
  @Test
  @DisplayName("create() : 자신이 작성한 댓글, 대댓글들 중에서 다른 사람이 댓글을 작성할 경우 알림이 온다.")
  void test_create_child_notification() throws Exception {
    //given
    final String content = "내용";
    final Long eventId = event.getId();
    final Member 댓글_작성자2 = memberRepository.findById(2L).get();
    final Member savedMember = memberRepository.save(new Member(200L, "imageUrl"));
    final Member 댓글_작성자3 = memberRepository.findById(savedMember.getId()).get();

    doNothing().when(eventPublisher).publish(any(UpdateNotificationEvent.class));

    final CommentAddRequest 부모_댓글_요청 = new CommentAddRequest(content, eventId, null);
    commentCommandService.create(부모_댓글_요청, 댓글_작성자);

    // 댓글_작성자에게 알림 1번
    final CommentAddRequest 자식_댓글_요청1 = new CommentAddRequest(content, eventId, 1L);
    commentCommandService.create(자식_댓글_요청1, 댓글_작성자2);

    // 댓글_작성자, 댓글_작성자2에게 알림 각각 1번
    final CommentAddRequest 자식_댓글_요청2 = new CommentAddRequest(content, eventId, 1L);
    commentCommandService.create(자식_댓글_요청2, 댓글_작성자3);

    final CommentAddRequest 알림이_가는_자식_댓글_요청 = new CommentAddRequest(content, eventId, 1L);

    //when  댓글_작성자, 댓글_작성자3에게 알림 각각 1번
    commentCommandService.create(알림이_가는_자식_댓글_요청, 댓글_작성자2);

    //then
    verify(eventPublisher, times(5)).publish(any(UpdateNotificationEvent.class));
  }

  @Test
  @DisplayName("create() : 삭제된 댓글에 대해서는 알림이 가지 않는다.")
  void test_create_not_notification_deletedComment() throws Exception {
    //given
    final String content = "내용";
    final Long eventId = event.getId();
    final Member 댓글_작성자2 = memberRepository.findById(2L).get();
    final Member savedMember = memberRepository.save(new Member(200L, "imageUrl"));
    final Member 댓글_작성자3 = memberRepository.findById(savedMember.getId()).get();

    doNothing().when(eventPublisher).publish(any(UpdateNotificationEvent.class));

    final CommentAddRequest 부모_댓글_요청 = new CommentAddRequest(content, eventId, null);
    commentCommandService.create(부모_댓글_요청, 댓글_작성자);

    // 댓글_작성자에게 알림 1번
    final CommentAddRequest 자식_댓글_요청1 = new CommentAddRequest(content, eventId, 1L);
    commentCommandService.create(자식_댓글_요청1, 댓글_작성자2);

    // 댓글_작성자, 댓글_작성자2에게 알림 각각 1번
    final CommentAddRequest 자식_댓글_요청2 = new CommentAddRequest(content, eventId, 1L);
    final CommentResponse response = commentCommandService.create(자식_댓글_요청2, 댓글_작성자3);
    commentCommandService.delete(response.getCommentId(), 댓글_작성자3);

    final CommentAddRequest 알림이_가는_자식_댓글_요청 = new CommentAddRequest(content, eventId, 1L);

    //when  댓글_작성자에게 알림 1번, 댓글_작성자3은 삭제된 댓글이므로 알림이 가지 않음
    commentCommandService.create(알림이_가는_자식_댓글_요청, 댓글_작성자2);

    //then
    verify(eventPublisher, times(4)).publish(any(UpdateNotificationEvent.class));
  }

  @Test
  @DisplayName("create() : 삭제된 댓글에 대해서는 알림이 가지 않는다.")
  void test_create_not_notification_deletedComment2() throws Exception {
    //given
    final String content = "내용";
    final Long eventId = event.getId();
    final Member 댓글_작성자2 = memberRepository.findById(2L).get();
    final Member savedMember = memberRepository.save(new Member(200L, "imageUrl"));
    final Member 댓글_작성자3 = memberRepository.findById(savedMember.getId()).get();

    doNothing().when(eventPublisher).publish(any(UpdateNotificationEvent.class));

    final CommentAddRequest 부모_댓글_요청 = new CommentAddRequest(content, eventId, null);
    final CommentResponse response = commentCommandService.create(부모_댓글_요청, 댓글_작성자);

    // 댓글_작성자에게 알림 1번
    final CommentAddRequest 자식_댓글_요청1 = new CommentAddRequest(content, eventId, 1L);
    commentCommandService.create(자식_댓글_요청1, 댓글_작성자2);

    // 댓글_작성자의 댓글 삭제
    commentCommandService.delete(response.getCommentId(), 댓글_작성자);

    // 댓글_작성자의 댓글은 삭제 -> 알림 X, 댓글_작성자2에게 알림 1번
    final CommentAddRequest 자식_댓글_요청2 = new CommentAddRequest(content, eventId, 1L);
    commentCommandService.create(자식_댓글_요청2, 댓글_작성자3);

    final CommentAddRequest 알림이_가는_자식_댓글_요청 = new CommentAddRequest(content, eventId, 1L);

    //when  댓글_작성자 삭제 -> 알림 X, 댓글_작성자3 알림 1번
    commentCommandService.create(알림이_가는_자식_댓글_요청, 댓글_작성자2);

    //then
    verify(eventPublisher, times(3)).publish(any(UpdateNotificationEvent.class));
  }

  @Test
  @DisplayName("delete() : 댓글을 삭제할 때, 로그인 한 본인의 댓글이 아니면 CAN_NOT_DELETE_COMMENT 가 발생합니다.")
  void test_delete_canNotDeleteComment() throws Exception {
    //given
    final Member 다른_사용자 = memberRepository.findById(2L).get();
    final Comment comment = commentRepository.save(Comment.createRoot(event, 댓글_작성자, "내용"));

    //when & then
    Assertions.assertThatThrownBy(
        () -> commentCommandService.delete(comment.getId(), 다른_사용자)
    ).hasMessage(CommentExceptionType.FORBIDDEN_DELETE_COMMENT.errorMessage());
  }

  @Test
  @DisplayName("delete() : 본인이 작성한 댓글을 삭제할 경우, 삭제 표시가 false -> true로 변경될 수 있다.")
  void test_delete() throws Exception {
    //given
    final Comment comment = commentRepository.save(Comment.createRoot(event, 댓글_작성자, "내용"));

    //when
    commentCommandService.delete(comment.getId(), 댓글_작성자);

    //then
    final Comment updatedComment = commentRepository.findById(comment.getId()).get();

    assertAll(
        () -> assertTrue(updatedComment.isDeleted()),
        () -> assertEquals("삭제된 댓글입니다.", updatedComment.getContent())
    );
  }

  @Test
  @DisplayName("modify() : 댓글을 수정할 때, 로그인 한 본인의 댓글이 아니면 CAN_NOT_UPDATE_COMMENT 가 발생합니다.")
  void test_modify_canNotModifyComment() throws Exception {
    //given
    final Member 다른_사용자 = memberRepository.findById(2L).get();
    final Comment comment = commentRepository.save(Comment.createRoot(event, 댓글_작성자, "내용"));

    final CommentModifyRequest request = new CommentModifyRequest("변경된 내용");

    //when & then
    Assertions.assertThatThrownBy(
        () -> commentCommandService.modify(comment.getId(), 다른_사용자, request)
    ).hasMessage(CommentExceptionType.FORBIDDEN_MODIFY_COMMENT.errorMessage());
  }

  @Test
  @DisplayName("modify() : 본인이 작성한 댓글을 수정할 수 있다.")
  void test_modify() throws Exception {
    //given
    final Comment comment = commentRepository.save(Comment.createRoot(event, 댓글_작성자, "내용"));

    final String modifiedContent = "변경된 내용";
    final CommentModifyRequest request = new CommentModifyRequest(modifiedContent);

    //when
    final CommentResponse response = commentCommandService.modify(comment.getId(), 댓글_작성자, request);

    //then
    assertAll(
        () -> assertEquals(comment.getId(), response.getCommentId()),
        () -> assertEquals(modifiedContent, response.getContent())
    );
  }

  @Test
  @DisplayName("modify() : 이미 삭제된 댓글은 삭제할 수 없습니다.")
  void test_modify_canNotModifyDeletedComment() throws Exception {
    //given
    final Comment comment = commentRepository.save(
        Comment.createRoot(event, 댓글_작성자, "내용")
    );

    final CommentModifyRequest request = new CommentModifyRequest("변경된 내용");
    comment.delete();

    final Comment deletedComment = commentRepository.save(comment);

    //when & then
    Assertions.assertThatThrownBy(
            () -> commentCommandService.modify(deletedComment.getId(), 댓글_작성자, request))
        .isInstanceOf(CommentException.class)
        .hasMessage(CommentExceptionType.FORBIDDEN_MODIFY_DELETED_COMMENT.errorMessage());
  }
}
