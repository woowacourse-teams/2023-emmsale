package com.emmsale.comment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.emmsale.event.EventFixture;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.feed.domain.Feed;
import com.emmsale.feed.domain.repository.FeedRepository;
import com.emmsale.helper.JpaRepositorySliceTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CommentRepositoryTest extends JpaRepositorySliceTestHelper {

  @Autowired
  private CommentRepository commentRepository;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private FeedRepository feedRepository;
  @Autowired
  private MemberRepository memberRepository;

  private Member member1;
  private Member member2;
  private Feed feed1;
  private Feed feed2;

  @BeforeEach
  void setUp() {
    final Event event1 = eventRepository.save(EventFixture.AI_컨퍼런스());
    final Event event2 = eventRepository.save(EventFixture.eventFixture());
    member1 = memberRepository.findById(1L).get();
    member2 = memberRepository.findById(2L).get();
    feed1 = feedRepository.save(new Feed(event1, member1, "피드 제목", "피드 내용"));
    feed2 = feedRepository.save(new Feed(event2, member1, "피드 제목", "피드 내용"));
  }

  @Test
  @DisplayName("findByEventId() : 행사에 존재하는 모든 댓글들을 조회할 수 있다.")
  void test_findByEventId() throws Exception {
    //given
    commentRepository.save(Comment.createRoot(feed1, member1, "부모댓글2"));
    commentRepository.save(Comment.createRoot(feed1, member1, "부모댓글1"));

    final Comment savedComment = commentRepository.save(
        Comment.createRoot(feed2, member1, "부모댓글1")
    );

    //when
    final List<Comment> savedComments = commentRepository.findByFeedId(feed2.getId());

    //then
    assertAll(
        () -> assertEquals(1, savedComments.size()),
        () -> assertEquals(savedComment.getId(), savedComments.get(0).getId())
    );
  }

  @Test
  @DisplayName("findParentAndChildrenByParentId() : 부모 ID가 주어졌을 때, 부모, 자식 댓글들을 모두 조회할 수 있다.")
  void test_findByParentId() throws Exception {
    //given
    final Comment parent = commentRepository.save(Comment.createRoot(feed1, member1, "부모댓글1"));
    final Comment 자식댓글1 = commentRepository.save(
        Comment.createChild(feed1, parent, member1, "자식댓글1"));
    final Comment 자식댓글2 = commentRepository.save(
        Comment.createChild(feed1, parent, member1, "자식댓글2"));

    //when
    final List<Comment> childrenComments = commentRepository.findParentAndChildrenByParentId(
        parent.getId());

    //then
    final List<Long> resultIds = childrenComments.stream()
        .map(Comment::getId)
        .collect(Collectors.toList());

    assertThat(resultIds).containsExactlyInAnyOrderElementsOf(
        List.of(parent.getId(), 자식댓글1.getId(), 자식댓글2.getId())
    );
  }

  @Test
  @DisplayName("findByMemberId() : 사용자가 작성한 댓글을 조회할 수 있다.")
  void test_findByMemberId() throws Exception {
    //given
    final Comment comment1 = commentRepository.save(
        Comment.createRoot(feed1, member1, "부모댓글1")
    );
    commentRepository.save(Comment.createChild(feed1, comment1, member2, "자식댓글1"));
    final Comment comment2 = commentRepository.save(
        Comment.createChild(feed1, comment1, member1, "자식댓글2")
    );

    final List<Comment> expected = List.of(comment1, comment2);

    //when
    final List<Comment> actual = commentRepository.findByMemberId(member1.getId());

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("createdAt")
        .isEqualTo(expected);
  }
}
