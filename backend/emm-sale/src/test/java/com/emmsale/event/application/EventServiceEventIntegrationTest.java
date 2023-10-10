package com.emmsale.event.application;

import static com.emmsale.tag.TagFixture.IOS;
import static com.emmsale.tag.TagFixture.백엔드;
import static com.emmsale.tag.TagFixture.안드로이드;
import static com.emmsale.tag.TagFixture.프론트엔드;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.emmsale.event.application.dto.EventDetailRequest;
import com.emmsale.event.domain.EventMode;
import com.emmsale.event.domain.EventType;
import com.emmsale.event.domain.PaymentType;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.InterestTag;
import com.emmsale.member.domain.InterestTagRepository;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.notification.domain.Notification;
import com.emmsale.tag.application.dto.TagRequest;
import com.emmsale.tag.domain.Tag;
import com.emmsale.tag.domain.TagRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class EventServiceEventIntegrationTest extends ServiceIntegrationTestHelper {

  @Autowired
  private EventService eventService;
  @Autowired
  private InterestTagRepository interestTagRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private TagRepository tagRepository;

  @Test
  @DisplayName("publish(Event) : 행사 이벤트가 발행되면 성공적으로 Event가 Listen 될 수 있다.")
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

    final EventDetailRequest eventDetailRequest = new EventDetailRequest(
        "인프콘 2023",
        "코엑스",
        "https://~~~",
        LocalDateTime.parse("2023-06-01T12:00:00"),
        LocalDateTime.parse("2023-09-01T12:00:00"),
        LocalDateTime.parse("2023-05-01T12:00:00"),
        LocalDateTime.parse("2023-06-01T12:00:00"),
        List.of(
            new TagRequest(안드로이드().getName()),
            new TagRequest(백엔드().getName())
        ),  // Assuming you don't have a direct TagRequest list from Event.
        EventType.CONFERENCE,
        EventMode.ON_OFFLINE,
        PaymentType.FREE_PAID,
        "인프런"
    );

    //when
    eventService.addEvent(eventDetailRequest, null);

    //then
    verify(firebaseCloudMessageClient, times(2))
        .sendMessageTo(any(Notification.class), anyLong());
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

    final EventDetailRequest eventDetailRequest = new EventDetailRequest(
        "인프콘 2023",
        "코엑스",
        "https://~~~",
        LocalDateTime.parse("2023-06-01T12:00:00"),
        LocalDateTime.parse("2023-09-01T12:00:00"),
        LocalDateTime.parse("2023-05-01T12:00:00"),
        LocalDateTime.parse("2023-06-01T12:00:00"),
        List.of(
            new TagRequest(IOS().getName())
        ),  // Assuming you don't have a direct TagRequest list from Event.
        EventType.CONFERENCE,
        EventMode.ON_OFFLINE,
        PaymentType.FREE_PAID,
        "인프런"
    );

    //when
    eventService.addEvent(eventDetailRequest, null);

    //then
    verify(firebaseCloudMessageClient, times(0))
        .sendMessageTo(any(Notification.class), anyLong());
  }

  @Test
  @DisplayName("publish(Event) : 행사 생성 이벤트가 발생했을 때, 사용자가 관심있어하는 태그가 없을 경우 알림이 가지 않는다.")
  void test_publish_event_no_notification_member_has_no_interest_tag() throws Exception {
    //given
    //member1 -> 태그 없음
    //IOS 태그를 가진 Event 추가
    //아무도 알림이 안감
    tagRepository.save(IOS());

    final EventDetailRequest eventDetailRequest = new EventDetailRequest(
        "인프콘 2023",
        "코엑스",
        "https://~~~",
        LocalDateTime.parse("2023-06-01T12:00:00"),
        LocalDateTime.parse("2023-09-01T12:00:00"),
        LocalDateTime.parse("2023-05-01T12:00:00"),
        LocalDateTime.parse("2023-06-01T12:00:00"),
        List.of(
            new TagRequest(IOS().getName())
        ),  // Assuming you don't have a direct TagRequest list from Event.
        EventType.CONFERENCE,
        EventMode.ON_OFFLINE,
        PaymentType.FREE_PAID,
        "인프런"
    );

    //when
    eventService.addEvent(eventDetailRequest, null);

    //then
    verify(firebaseCloudMessageClient, times(0))
        .sendMessageTo(any(Notification.class), anyLong());
  }
}
