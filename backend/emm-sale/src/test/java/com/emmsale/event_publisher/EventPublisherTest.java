package com.emmsale.event_publisher;

import static com.emmsale.tag.TagFixture.IOS;
import static com.emmsale.tag.TagFixture.백엔드;
import static com.emmsale.tag.TagFixture.안드로이드;
import static com.emmsale.tag.TagFixture.프론트엔드;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.emmsale.comment.event.UpdateNotificationEvent;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventType;
import com.emmsale.member.domain.InterestTag;
import com.emmsale.member.domain.InterestTagRepository;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.tag.domain.Tag;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

class EventPublisherTest {

  private EventPublisher eventPublisher;
  private ApplicationEventPublisher applicationEventPublisher;
  private InterestTagRepository interestTagRepository;
  private MemberRepository memberRepository;
  private Member member1, member2, member3;

  @BeforeEach
  void setUp() {
    applicationEventPublisher = mock(ApplicationEventPublisher.class);
    interestTagRepository = mock(InterestTagRepository.class);
    memberRepository = mock(MemberRepository.class);

    eventPublisher = new EventPublisher(
        applicationEventPublisher,
        interestTagRepository,
        memberRepository
    );

    member1 = spy(new Member(222L, "imageUrl"));
    when(member1.getId()).thenReturn(1L);
    member2 = spy(new Member(223L, "imageUrl"));
    when(member2.getId()).thenReturn(2L);
    member3 = spy(new Member(224L, "imageUrl"));
    when(member3.getId()).thenReturn(3L);
  }

  @Test
  @DisplayName("publish(Event) : 행사 생성 이벤트가 발생했을 때, 사용자가 관심있어하는 태그가 행사에 포함될 경우 알림을 보낼 수 있다.")
  void test_publish_event() throws Exception {
    //given
    //member1 -> 안드로이드, member2 -> 안드로이드 백엔드, member3 -> 프론트엔드
    //안드로이드, 백엔드 태그를 가진 Event 추가
    //member1, member2만 알림이 감
    when(memberRepository.findAll())
        .thenReturn(List.of(member1, member2, member3));

    final List<InterestTag> member1Tags = List.of(
        new InterestTag(member1, new Tag(안드로이드().getName()))
    );

    when(interestTagRepository.findInterestTagsByMemberId(member1.getId()))
        .thenReturn(member1Tags);

    final List<InterestTag> member2Tags = List.of(
        new InterestTag(member2, new Tag(안드로이드().getName())),
        new InterestTag(member2, new Tag(백엔드().getName()))
    );

    when(interestTagRepository.findInterestTagsByMemberId(member2.getId()))
        .thenReturn(member2Tags);

    final List<InterestTag> member3Tags = List.of(
        new InterestTag(member3, new Tag(프론트엔드().getName()))
    );

    when(interestTagRepository.findInterestTagsByMemberId(member3.getId()))
        .thenReturn(member3Tags);

    final Event event = new Event(
        "name",
        "location",
        LocalDateTime.now(),
        LocalDateTime.now(),
        LocalDateTime.now().minusDays(15L),
        LocalDateTime.now().minusDays(10L),
        "informationUrl",
        EventType.CONFERENCE,
        "imageUrl"
    );

    event.addAllEventTags(
        List.of(new Tag(백엔드().getName()), new Tag(안드로이드().getName()))
    );

    List<Long> expectedNotificationIds = List.of(1L, 2L);

    //when
    eventPublisher.publish(event);

    //then
    ArgumentCaptor<UpdateNotificationEvent> captor = ArgumentCaptor.forClass(
        UpdateNotificationEvent.class);

    verify(applicationEventPublisher, times(2)).publishEvent(captor.capture());

    final List<Long> actual = captor.getAllValues()
        .stream()
        .map(UpdateNotificationEvent::getReceiverId)
        .collect(Collectors.toList());

    Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expectedNotificationIds);
  }

  @Test
  @DisplayName("publish(Event) : 행사 생성 이벤트가 발생했을 때, 사용자가 관심있어하는 태그가 행사에 없을 경우 알림이 가지 않는다.")
  void test_publish_event_no_notification_event_has_no_interest_tag() throws Exception {
    //given
    //member1 -> 안드로이드, member2 -> 안드로이드 백엔드, member3 -> 프론트엔드
    //IOS 태그를 가진 Event 추가
    //아무도 알림이 안감
    when(memberRepository.findAll())
        .thenReturn(List.of(member1, member2, member3));

    final List<InterestTag> member1Tags = List.of(
        new InterestTag(member1, new Tag(안드로이드().getName()))
    );

    when(interestTagRepository.findInterestTagsByMemberId(member1.getId()))
        .thenReturn(member1Tags);

    final List<InterestTag> member2Tags = List.of(
        new InterestTag(member2, new Tag(안드로이드().getName())),
        new InterestTag(member2, new Tag(백엔드().getName()))
    );

    when(interestTagRepository.findInterestTagsByMemberId(member2.getId()))
        .thenReturn(member2Tags);

    final List<InterestTag> member3Tags = List.of(
        new InterestTag(member3, new Tag(프론트엔드().getName()))
    );

    when(interestTagRepository.findInterestTagsByMemberId(member3.getId()))
        .thenReturn(member3Tags);

    final Event event = new Event(
        "name",
        "location",
        LocalDateTime.now(),
        LocalDateTime.now(),
        LocalDateTime.now().minusDays(15L),
        LocalDateTime.now().minusDays(10L),
        "informationUrl",
        EventType.CONFERENCE,
        "imageUrl"
    );

    event.addAllEventTags(
        List.of(new Tag(IOS().getName()))
    );

    //when
    eventPublisher.publish(event);

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
    when(memberRepository.findAll())
        .thenReturn(List.of(member1));

    when(interestTagRepository.findInterestTagsByMemberId(member1.getId()))
        .thenReturn(Collections.emptyList());

    final Event event = new Event(
        "name",
        "location",
        LocalDateTime.now(),
        LocalDateTime.now(),
        LocalDateTime.now().minusDays(15L),
        LocalDateTime.now().minusDays(10L),
        "informationUrl",
        EventType.CONFERENCE,
        "imageUrl"
    );

    event.addAllEventTags(
        List.of(new Tag(IOS().getName()))
    );

    //when
    eventPublisher.publish(event);

    //then
    verify(applicationEventPublisher, times(0))
        .publishEvent(any(UpdateNotificationEvent.class));
  }
}
