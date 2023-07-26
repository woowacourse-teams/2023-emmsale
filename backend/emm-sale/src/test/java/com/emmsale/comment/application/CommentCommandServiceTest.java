package com.emmsale.comment.application;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.emmsale.comment.application.dto.CommentAddRequest;
import com.emmsale.comment.application.dto.CommentModifyRequest;
import com.emmsale.comment.application.dto.CommentResponse;
import com.emmsale.comment.domain.Comment;
import com.emmsale.comment.domain.CommentRepository;
import com.emmsale.comment.exception.CommentException;
import com.emmsale.comment.exception.CommentExceptionType;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CommentCommandServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private CommentCommandService commentCommandService;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private CommentRepository commentRepository;

  private Event event;
  private Member 댓글_작성자;

  @BeforeEach
  void init() {
    event = eventRepository.save(new Event(
        "event", "location",
        LocalDateTime.now(), LocalDateTime.now(),
        "url"
    ));

    댓글_작성자 = memberRepository.findById(1L).get();
  }

  @Test
  @DisplayName("create() : 댓글 부모 id가 없으면 Root(최상단) 댓글을 생성할 수 있다.")
  void test_create() throws Exception {
    //given
    final String content = "내용";

    final CommentAddRequest 부모_댓글_요청 = new CommentAddRequest(content, event.getId(), null);

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
  @DisplayName("create() : 댓글 부모 id가 있으면 그 자식 댓글(대댓글)을 생성할 수 있다.")
  void test_create_child() throws Exception {
    //given
    final String content = "내용";
    final Long eventId = event.getId();

    final CommentAddRequest 부모_댓글_요청 = new CommentAddRequest(content, eventId, null);
    final CommentResponse 부모_댓글_응답 =
        commentCommandService.create(부모_댓글_요청, 댓글_작성자);
    final CommentAddRequest 자식_댓글_요청 = new CommentAddRequest(content, eventId, 1L);

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
        () -> assertEquals(updatedComment.getContent(), "삭제된 댓글입니다.")
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
