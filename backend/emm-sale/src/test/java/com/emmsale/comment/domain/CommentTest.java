package com.emmsale.comment.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.emmsale.event.EventFixture;
import com.emmsale.event.domain.Event;
import com.emmsale.member.MemberFixture;
import com.emmsale.member.domain.Member;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommentTest {

  private static final String BLOCKED_MEMBER_CONTENT = "차단된 사용자의 댓글입니다.";

  private Event event;
  private Member writer;
  private Member blockedMember;

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
    final String actual = comment.getContentAndHideIfBlockedMember(emptyBlockedMemberIds);

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
    final String actual = comment.getContentAndHideIfBlockedMember(emptyBlockedMemberIds);

    //then
    assertEquals(BLOCKED_MEMBER_CONTENT, actual);
  }

}
