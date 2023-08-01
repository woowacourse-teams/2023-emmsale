package com.emmsale.comment.application;

import com.emmsale.comment.application.dto.CommentHierarchyResponse;
import com.emmsale.comment.application.dto.CommentResponse;
import com.emmsale.comment.domain.Comment;
import com.emmsale.comment.domain.CommentRepository;
import com.emmsale.event.EventFixture;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CommentQueryServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private CommentQueryService commentQueryService;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private CommentRepository commentRepository;
  private Event event;
  private Member member;
  private Comment 부모_댓글1;
  private Comment 부모_댓글2;

  @BeforeEach
  void init() {
    event = eventRepository.save(
        EventFixture.모바일_컨퍼런스()
    );
    member = memberRepository.findById(1L).get();

    부모_댓글2 = commentRepository.save(
        Comment.createRoot(event, member, "부모댓글2")
    );

    부모_댓글1 = commentRepository.save(
        Comment.createRoot(event, member, "부모댓글1")
    );
  }

  @Test
  @DisplayName("findByEventId() : 행사에 존재하는 댓글들을 모두 조회할 수 있다.")
  void test_findByEventId() throws Exception {
    //given
    final Comment comment1 = Comment.createChild(event, 부모_댓글1, member, "부모댓글1에 대한 자식댓글1");
    final Comment comment2 = Comment.createChild(event, 부모_댓글1, member, "부모댓글1에 대한 자식댓글2");
    commentRepository.save(comment2);
    commentRepository.save(comment1);

    final List<CommentHierarchyResponse> expected = List.of(
        new CommentHierarchyResponse(
            CommentResponse.from(부모_댓글2),
            Collections.EMPTY_LIST
        ),
        new CommentHierarchyResponse(
            CommentResponse.from(부모_댓글1),
            List.of(
                CommentResponse.from(comment2),
                CommentResponse.from(comment1)
            )
        )
    );

    //when
    final List<CommentHierarchyResponse> actual = commentQueryService.findAllCommentsByEventId(
        event.getId());

    //then
    Assertions.assertThat(expected)
        .usingRecursiveComparison()
        .isEqualTo(actual);
  }
}
