package com.emmsale.comment.application;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.emmsale.comment.application.dto.CommentAddRequest;
import com.emmsale.comment.application.dto.CommentResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import java.time.LocalDateTime;
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

  private Event event;

  @BeforeEach
  void init() {
    event = eventRepository.save(new Event(
        "event", "location",
        LocalDateTime.now(), LocalDateTime.now(),
        "url", null
    ));
  }

  @Test
  @DisplayName("create() : 댓글 부모 id가 없으면 Root(최상단) 댓글을 생성할 수 있다.")
  void test_create() throws Exception {
    //given
    final Member member = memberRepository.findById(1L).get();
    final String content = "내용";

    final CommentAddRequest 부모_댓글_요청 = new CommentAddRequest(content, event.getId(), null);

    //when
    final CommentResponse 부모_댓글_응답 = commentCommandService.create(부모_댓글_요청, member);

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
    final Member member = memberRepository.findById(1L).get();
    final String content = "내용";
    final Long eventId = event.getId();

    final CommentAddRequest 부모_댓글_요청 = new CommentAddRequest(content, eventId, null);
    final CommentResponse 부모_댓글_응답 =
        commentCommandService.create(부모_댓글_요청, member);
    final CommentAddRequest 자식_댓글_요청 = new CommentAddRequest(content, eventId, 1L);

    //when
    final CommentResponse 자식_댓글_응답 = commentCommandService.create(자식_댓글_요청, member);

    //then
    assertAll(
        () -> assertEquals(자식_댓글_응답.getContent(), content),
        () -> assertNotNull(자식_댓글_응답.getCommentId()),
        () -> assertEquals(부모_댓글_응답.getCommentId(), 자식_댓글_응답.getParentId())
    );
  }
}
