package com.emmsale.event_publisher;

import static com.emmsale.tag.TagFixture.IOS;
import static com.emmsale.tag.TagFixture.백엔드;
import static com.emmsale.tag.TagFixture.안드로이드;
import static com.emmsale.tag.TagFixture.프론트엔드;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.emmsale.comment.domain.Comment;
import com.emmsale.comment.domain.CommentRepository;
import com.emmsale.comment.event.UpdateNotificationEvent;
import com.emmsale.event.EventFixture;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event_publisher.EventPublisherTest.TestConfig;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.InterestTag;
import com.emmsale.member.domain.InterestTagRepository;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.tag.domain.Tag;
import com.emmsale.tag.domain.TagRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Import(TestConfig.class)
class EventPublisherTest extends ServiceIntegrationTestHelper {

  @Autowired
  private EventPublisher eventPublisher;
  @Autowired
  private InterestTagRepository interestTagRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private TagRepository tagRepository;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private CommentRepository commentRepository;
  @MockBean
  private ApplicationEventPublisher applicationEventPublisher;

  private Event event = EventFixture.인프콘_2023();

  @Test
  @DisplayName("publish(Event) : 행사 생성 이벤트가 발생했을 때, 사용자가 관심있어하는 태그가 행사에 포함될 경우 알림을 보낼 수 있다.")
  void test_publish_event() throws Exception {
    //given
    //member1 -> 안드로이드, member2 -> 안드로이드 백엔드, member3 -> 프론트엔드
    //안드로이드, 백엔드 태그를 가진 Event 추가
    //member1, member2만 알림이 감

    final Member member1 = memberRepository.findById(1L).get();
    final Member member2 = memberRepository.findById(2L).get();
    final Member member3 = memberRepository.save(new Member(1444L, "imageUrl", "username"));
    final Tag 안드로이드 = tagRepository.save(안드로이드());
    final Tag 백엔드 = tagRepository.save(백엔드());
    final Tag 프론트엔드 = tagRepository.save(프론트엔드());
    interestTagRepository.save(new InterestTag(member1, 안드로이드));
    interestTagRepository.save(new InterestTag(member2, 안드로이드));
    interestTagRepository.save(new InterestTag(member2, 백엔드));
    interestTagRepository.save(new InterestTag(member3, 프론트엔드));

    final List<Long> expected = List.of(member1.getId(), member2.getId());

    event.addAllEventTags(List.of(백엔드, 안드로이드));

    final Event savedEvent = eventRepository.save(event);

    //when
    eventPublisher.publish(savedEvent);

    //then
    ArgumentCaptor<UpdateNotificationEvent> captor = ArgumentCaptor.forClass(
        UpdateNotificationEvent.class);

    verify(applicationEventPublisher, times(2)).publishEvent(captor.capture());

    final List<Long> actual = captor.getAllValues()
        .stream()
        .map(UpdateNotificationEvent::getReceiverId)
        .collect(Collectors.toList());

    Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  @DisplayName("publish(Event) : 행사 생성 이벤트가 발생했을 때, 사용자가 관심있어하는 태그가 행사에 없을 경우 알림이 가지 않는다.")
  void test_publish_event_no_notification_event_has_no_interest_tag() throws Exception {
    //given
    //member1 -> 안드로이드, member2 -> 안드로이드 백엔드, member3 -> 프론트엔드
    //IOS 태그를 가진 Event 추가
    //아무도 알림이 안감
    final Member member1 = memberRepository.findById(1L).get();
    final Member member2 = memberRepository.findById(2L).get();
    final Member member3 = memberRepository.save(new Member(1444L, "imageUrl", "username"));
    final Tag 안드로이드 = tagRepository.save(안드로이드());
    final Tag 백엔드 = tagRepository.save(백엔드());
    final Tag 프론트엔드 = tagRepository.save(프론트엔드());
    final Tag IOS = tagRepository.save(IOS());
    interestTagRepository.save(new InterestTag(member1, 안드로이드));
    interestTagRepository.save(new InterestTag(member2, 안드로이드));
    interestTagRepository.save(new InterestTag(member2, 백엔드));
    interestTagRepository.save(new InterestTag(member3, 프론트엔드));

    event.addAllEventTags(List.of(IOS));

    final Event savedEvent = eventRepository.save(event);

    //when
    eventPublisher.publish(savedEvent);

    //then
    verify(applicationEventPublisher, times(0))
        .publishEvent(any(UpdateNotificationEvent.class));
  }

  @Test
  @DisplayName("publish(Event) : 행사 생성 이벤트가 발생했을 때, 사용자가 관심있어하는 태그가 없을 경우 알림이 가지 않는다.")
  void test_publish_event_no_notification_member_has_no_interest_tag() throws Exception {
    //given
    //member1 -> 태그 없음
    //IOS 태그를 가진 Event 추가
    //아무도 알림이 안감
    final Member member1 = memberRepository.findById(1L).get();
    final Tag IOS = tagRepository.save(IOS());

    event.addAllEventTags(List.of(IOS));

    final Event savedEvent = eventRepository.save(event);

    //when
    eventPublisher.publish(savedEvent);

    //then
    verify(applicationEventPublisher, times(0))
        .publishEvent(any(UpdateNotificationEvent.class));
  }

  /**
   * 1 ㄴ2 ㄴ3
   * <p>
   * 새로운 대댓글 ㄴ2  (1,3에게 알림)
   */
  @Test
  @DisplayName("publish(Comment) : 대댓글이 생성되면 부모와 그 자식 댓글들 중에서 사용자가 작성한 댓글을 제외한 나머지 사용자들에게 알림을 보낼 수 있다.")
  void test_publish_comment_parent_children() throws Exception {
    //given
    final Member 댓글_작성자1 = memberRepository.findById(1L).get();
    final Member 로그인_사용자 = memberRepository.save(new Member(13445L, "image", "username1"));
    final Member 댓글_작성자2 = memberRepository.save(new Member(13444L, "image", "username"));
    eventRepository.save(event);
    final Comment 부모_댓글 = commentRepository.save(Comment.createRoot(event, 댓글_작성자1, "내용1"));
    final Comment 자식_댓글1 = commentRepository.save(
        Comment.createChild(event, 부모_댓글, 로그인_사용자, "내용2"));
    final Comment 자식_댓글2 = commentRepository.save(
        Comment.createChild(event, 부모_댓글, 댓글_작성자2, "내용3"));
    final Comment 알림_트리거_댓글 = commentRepository.save(
        Comment.createChild(event, 부모_댓글, 로그인_사용자, "내용4"));

    doNothing().when(applicationEventPublisher).publishEvent(any());

    //when
    eventPublisher.publish(알림_트리거_댓글, 로그인_사용자);

    //then
    verify(applicationEventPublisher, times(2))
        .publishEvent(any(UpdateNotificationEvent.class));
  }

  @Test
  @DisplayName("publish(Comment) : 대댓글을 생성할 경우 부모, 자식 댓글에서 나의 댓글만 존재한다면 알림이 가지 않는다.")
  void test_publish_comment_parent_children_not_notification() throws Exception {
    //given
    final Member 댓글_작성자1 = memberRepository.findById(1L).get();
    eventRepository.save(event);
    final Comment 부모_댓글 = commentRepository.save(
        Comment.createRoot(event, 댓글_작성자1, "내용1")
    );
    final Comment 자식_댓글1 = commentRepository.save(
        Comment.createChild(event, 부모_댓글, 댓글_작성자1, "내용2")
    );
    final Comment 알림_트리거_댓글 = commentRepository.save(
        Comment.createChild(event, 부모_댓글, 댓글_작성자1, "내용4")
    );

    //when
    eventPublisher.publish(알림_트리거_댓글, 댓글_작성자1);

    //then
    verify(applicationEventPublisher, times(0))
        .publishEvent(any(UpdateNotificationEvent.class));
  }

  @Test
  @DisplayName("publish(Comment) : 자신이 작성한 댓글이 Root 댓글이라면 알림이 가지 않는다.")
  void test_publish_root_comment_no_notification() throws Exception {
    //given
    final Member 댓글_작성자1 = memberRepository.findById(1L).get();
    eventRepository.save(event);
    final Comment 알림_트리거_댓글 = commentRepository.save(
        Comment.createRoot(event, 댓글_작성자1, "내용1")
    );

    //when
    eventPublisher.publish(알림_트리거_댓글, 댓글_작성자1);

    //then
    verify(applicationEventPublisher, times(0))
        .publishEvent(any(UpdateNotificationEvent.class));
  }

  @Test
  @DisplayName("publish(Comment) : 삭제된 댓글에 대해서는 알림이 가지 않는다.")
  void test_publish_comment_not_notification_deletedComment() throws Exception {
    //given
    eventRepository.save(event);
    final Member 로그인_사용자 = memberRepository.findById(1L).get();
    final Member 댓글_작성자1 = memberRepository.findById(2L).get();
    final Member savedMember = memberRepository.save(new Member(200L, "imageUrl", "아마란스"));
    final Member 댓글_작성자2 = memberRepository.findById(savedMember.getId()).get();

    final Comment 부모_댓글 = commentRepository.save(
        Comment.createRoot(event, 로그인_사용자, "내용1")
    );
    final Comment 자식_댓글1 = commentRepository.save(
        Comment.createChild(event, 부모_댓글, 댓글_작성자1, "내용2")
    );
    final Comment 자식_댓글2 = commentRepository.save(
        Comment.createChild(event, 부모_댓글, 댓글_작성자2, "내용3")
    );
    자식_댓글2.delete();
    commentRepository.save(자식_댓글2);

    final Comment 알림_트리거_댓글 = commentRepository.save(
        Comment.createChild(event, 부모_댓글, 로그인_사용자, "내용3")
    );

    //when
    eventPublisher.publish(알림_트리거_댓글, 로그인_사용자);

    //then
    verify(applicationEventPublisher, times(1))
        .publishEvent(any(UpdateNotificationEvent.class));
  }

  @Test
  @DisplayName("publish(Comment) : 삭제된 댓글에 대해서는 알림이 가지 않는다.")
  void test_publish_comment_not_notification_deletedComment2() throws Exception {
    //given
    eventRepository.save(event);
    final Member 로그인_사용자 = memberRepository.findById(1L).get();
    final Member 댓글_작성자1 = memberRepository.findById(2L).get();
    final Member savedMember = memberRepository.save(new Member(200L, "imageUrl", "아마란스"));
    final Member 댓글_작성자2 = memberRepository.findById(savedMember.getId()).get();

    final Comment 부모_댓글 = commentRepository.save(
        Comment.createRoot(event, 로그인_사용자, "내용1")
    );
    final Comment 자식_댓글1 = commentRepository.save(
        Comment.createChild(event, 부모_댓글, 댓글_작성자1, "내용2")
    );
    final Comment 자식_댓글2 = commentRepository.save(
        Comment.createChild(event, 부모_댓글, 댓글_작성자2, "내용3")
    );
    부모_댓글.delete();
    commentRepository.save(부모_댓글);

    final Comment 알림_트리거_댓글 = commentRepository.save(
        Comment.createChild(event, 부모_댓글, 로그인_사용자, "내용3")
    );

    //when
    eventPublisher.publish(알림_트리거_댓글, 로그인_사용자);

    //then
    verify(applicationEventPublisher, times(2))
        .publishEvent(any(UpdateNotificationEvent.class));
  }


  @TestConfiguration
  static class TestConfig {

    @Bean
    @Primary
    public ApplicationEventPublisher publisher() {
      return mock(ApplicationEventPublisher.class);
    }
  }
}
