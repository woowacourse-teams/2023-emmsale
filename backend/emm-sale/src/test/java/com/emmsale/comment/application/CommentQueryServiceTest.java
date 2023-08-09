package com.emmsale.comment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.emmsale.block.domain.Block;
import com.emmsale.block.domain.BlockRepository;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
  @Autowired
  private BlockRepository blockRepository;
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
        event.getId(), member);

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

  @Test
  @DisplayName("findParentWithChildren() : 부모 댓글에 있는 자식 댓글들을 모두 조회할 수 있다.")
  void test_findChildrenComments() throws Exception {
    //given
    final Comment 자식댓글2 = commentRepository.save(
        Comment.createChild(event, 부모_댓글1, member, "자식댓글2"));
    final Comment 자식댓글1 = commentRepository.save(
        Comment.createChild(event, 부모_댓글1, member, "자식댓글1"));

    final CommentHierarchyResponse expected =
        new CommentHierarchyResponse(
            CommentResponse.from(부모_댓글1),
            List.of(
                CommentResponse.from(자식댓글2),
                CommentResponse.from(자식댓글1)
            )
        );

    //when
    final CommentHierarchyResponse actual =
        commentQueryService.findParentWithChildren(부모_댓글1.getId(), member);

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

  @Nested
  @DisplayName("차단된 사용자의 댓글 조회 테스트")
  class HideContent {

    private static final String BLOCKED_MEMBER_CONTENT = "차단된 사용자의 댓글입니다.";

    @Test
    @DisplayName("차단한 사용자의 루트 댓글 내용을 `BLOCKED_MEMBER_CONTENT`으로 대체한다.")
    void blockedUserCommentsQuery1() {
      //given
      final long blockedMemberId = 2L;
      final Member blockedMember = memberRepository.findById(blockedMemberId).get();

      commentRepository.save(Comment.createRoot(event, blockedMember, "차단한 사용자의 루트 댓글"));

      final Block block = new Block(member.getId(), blockedMemberId);
      blockRepository.save(block);

      //when
      final List<CommentHierarchyResponse> result = commentQueryService.findAllCommentsByEventId(
          event.getId(), member);

      //then
      final int lastIndexOfResult = result.size() - 1;
      final String actualBlockedMemberContent = result.get(lastIndexOfResult).getParentComment()
          .getContent();
      assertEquals(BLOCKED_MEMBER_CONTENT, actualBlockedMemberContent);
    }

    @Test
    @DisplayName("차단하지 않은 사용자의 루트 댓글에 달린 차단한 사용자의 대댓글 내용을 `BLOCKED_MEMBER_CONTENT`으로 대체한다.")
    void blockedUserCommentsQuery2() {
      //given
      final long blockedMemberId = 2L;
      final Member blockedMember = memberRepository.findById(blockedMemberId).get();

      commentRepository.save(Comment.createChild(event, 부모_댓글1, blockedMember, "차단한 사용자의 대댓글"));

      final Block block = new Block(member.getId(), blockedMemberId);
      blockRepository.save(block);

      //when
      final List<CommentHierarchyResponse> result = commentQueryService.findAllCommentsByEventId(
          event.getId(), member);

      //then
      final String actualBlockedMemberContent = result.get(1).getChildComments().get(0)
          .getContent();
      assertEquals(BLOCKED_MEMBER_CONTENT, actualBlockedMemberContent);
    }
  }
}
