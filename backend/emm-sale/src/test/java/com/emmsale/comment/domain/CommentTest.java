package com.emmsale.comment.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.emmsale.comment.exception.CommentException;
import com.emmsale.comment.exception.CommentExceptionType;
import com.emmsale.event.EventFixture;
import com.emmsale.event.domain.Event;
import com.emmsale.member.MemberFixture;
import com.emmsale.member.domain.Member;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CommentTest {

  private static final String BLOCKED_MEMBER_CONTENT = "차단된 사용자의 댓글입니다.";

  private Event event;
  private Member writer;
  private Member blockedMember;

  static Stream<Arguments> commentCandidate() {
    final Event event = EventFixture.인프콘_2023();
    final Member writer = MemberFixture.memberFixture();

    final Comment comment1 = Comment.createRoot(event, writer, "댓글 내용");
    final Comment comment2 = Comment.createRoot(event, writer, "댓글");
    comment2.delete();
    final Comment comment3 = Comment.createRoot(event, writer, "삭제된 댓글입니다.");

    return Stream.of(
        Arguments.of(comment1, true),
        Arguments.of(comment2, false),
        Arguments.of(comment3, true)
    );
  }

  @BeforeEach
  void init() {
    event = EventFixture.인프콘_2023();

    writer = spy(MemberFixture.memberFixture());
    when(writer.getId()).thenReturn(1L);

    blockedMember = spy(MemberFixture.memberFixture());
    when(blockedMember.getId()).thenReturn(2L);
  }

  @Test
  @DisplayName("Comment 작성자가 차단 목록에 없을 경우 원본 content를 반환한다.")
  void getContentAndHideIfBlockedMemberWithEmptyBlockedMemberList() {
    //given
    final String expectContent = "댓글 내용";
    final Comment comment = Comment.createRoot(event, blockedMember, expectContent);

    final List<Long> emptyBlockedMemberIds = Collections.emptyList();

    //when
    final String actual = comment.getContentOrHideIfBlockedMember(emptyBlockedMemberIds);

    //then
    assertEquals(expectContent, actual);
  }

  @Test
  @DisplayName("Comment 작성자가 차단된 사용자일 경우 content를 `BLOCKED_MEMBER_CONTENT`으로 반환한다.")
  void getContentAndHideIfBlockedMemberWithBlockedMember() {
    //given
    final String expectContent = "댓글 내용";
    final Comment comment = Comment.createRoot(event, blockedMember, expectContent);

    final List<Long> emptyBlockedMemberIds = List.of(blockedMember.getId());

    //when
    final String actual = comment.getContentOrHideIfBlockedMember(emptyBlockedMemberIds);

    //then
    assertEquals(BLOCKED_MEMBER_CONTENT, actual);
  }

  @ParameterizedTest
  @MethodSource("commentCandidate")
  @DisplayName("isNotDeleted() : 삭제 표시가 false이고 내용이 삭제된 댓글이 아닐 경우에 삭제되지 않은 댓글이라고 할 수 있다.")
  void test_isNotDeleted(final Comment comment, final boolean result) throws Exception {
    //when & then
    assertEquals(comment.isNotDeleted(), result);
  }

  @Test
  @DisplayName("createChild() : 대대댓글은 작성할 경우 NOT_CREATE_CHILD_CHILD_COMMENT 발생할 수 있다.")
  void test_createChild_not_create_child_child_comment() throws Exception {
    //given
    final Event event = EventFixture.구름톤();
    final Member member = new Member(1333L, "imageUrl", "usrename");

    final Comment root = Comment.createRoot(event, member, "부모내용");
    final Comment child1 = Comment.createChild(event, root, member, "자식내용1");
    final Comment child2 = Comment.createChild(event, root, member, "자식내용2");

    //when & then
    assertThatThrownBy(() -> Comment.createChild(event, child1, member, "자자식댓글1"))
        .isInstanceOf(CommentException.class)
        .hasMessage(CommentExceptionType.NOT_CREATE_CHILD_CHILD_COMMENT.errorMessage());
  }
}
