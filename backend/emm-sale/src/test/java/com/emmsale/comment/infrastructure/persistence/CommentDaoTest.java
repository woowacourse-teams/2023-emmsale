package com.emmsale.comment.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.comment.domain.Comment;
import com.emmsale.comment.domain.CommentRepository;
import com.emmsale.comment.infrastructure.persistence.dto.FeedCommentCount;
import com.emmsale.event.EventFixture;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.feed.domain.Feed;
import com.emmsale.feed.domain.repository.FeedRepository;
import com.emmsale.helper.JpaRepositorySliceTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

class CommentDaoTest extends JpaRepositorySliceTestHelper {

  @Autowired
  private DataSource dataSource;
  @Autowired
  private FeedRepository feedRepository;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private CommentRepository commentRepository;

  private CommentDao commentDao;
  private Member member1;
  private Member member2;
  private Feed feed1;
  private Feed feed2;

  @BeforeEach
  void setUp() {
    commentDao = new CommentDao(new NamedParameterJdbcTemplate(dataSource));

    member1 = memberRepository.findById(1L).get();
    member2 = memberRepository.findById(2L).get();

    final Event event1 = eventRepository.save(EventFixture.AI_컨퍼런스());
    final Event event2 = eventRepository.save(EventFixture.eventFixture());

    feed1 = feedRepository.save(new Feed(event1, member1, "피드 제목", "피드 내용"));
    feed2 = feedRepository.save(new Feed(event2, member1, "피드 제목", "피드 내용"));
  }

  @Test
  @DisplayName("findCommentCountByFeedIds(): 모든 피드의 id를 받아 댓글이 달린 피드의 id와 댓글의 개수를 반환한다.")
  void findCommentCountByFeedIdsTest() {
    //given
    final Comment parent = commentRepository.save(Comment.createRoot(feed1, member1, "부모댓글1"));
    commentRepository.save(Comment.createChild(feed1, parent, member2, "자식댓글1"));
    commentRepository.save(Comment.createRoot(feed2, member1, "부모댓글1"));

    final List<FeedCommentCount> expect = List.of(
        new FeedCommentCount(feed1.getId(), 2L),
        new FeedCommentCount(feed2.getId(), 1L)
    );

    //when
    final List<Long> feedIds = List.of(feed1.getId(), feed2.getId());
    final List<FeedCommentCount> actual = commentDao.findCommentCountByFeedIds(feedIds);

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);
  }

  @Test
  @DisplayName("findCommentCountByFeedIds(): 삭제된 댓글은 개수에 포함되지 않는다.")
  void findCommentCountByFeedIdsWithDeletedCommentTest() {
    //given
    final Comment parent = commentRepository.save(Comment.createRoot(feed1, member1, "부모댓글1"));
    final Comment deletedComment = Comment.createChild(feed1, parent, member2, "자식댓글1");
    deletedComment.delete();
    commentRepository.save(deletedComment);

    final List<FeedCommentCount> expect = List.of(
        new FeedCommentCount(feed1.getId(), 1L)
    );

    //when
    final List<Long> feedIds = List.of(feed1.getId());
    final List<FeedCommentCount> actual = commentDao.findCommentCountByFeedIds(feedIds);

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);
  }

  @Test
  @DisplayName("findCommentCountByFeedIds(): 댓글이 없는 피드는 집계되지 않는다.")
  void findCommentCountByFeedIdsWithNoCommentFeedCommentTest() {
    //given
    final List<FeedCommentCount> expect = List.of();

    //when
    final List<Long> feedIds = List.of(feed1.getId());
    final List<FeedCommentCount> actual = commentDao.findCommentCountByFeedIds(feedIds);

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);
  }
}
