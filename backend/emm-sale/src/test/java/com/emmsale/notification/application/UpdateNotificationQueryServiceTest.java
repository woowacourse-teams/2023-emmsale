package com.emmsale.notification.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.comment.domain.Comment;
import com.emmsale.comment.domain.CommentRepository;
import com.emmsale.event.EventFixture;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.notification.application.dto.UpdateNotificationResponse;
import com.emmsale.notification.application.dto.UpdateNotificationResponse.CommentTypeNotification;
import com.emmsale.notification.domain.UpdateNotification;
import com.emmsale.notification.domain.UpdateNotificationRepository;
import com.emmsale.notification.domain.UpdateNotificationType;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UpdateNotificationQueryServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private UpdateNotificationQueryService updateNotificationQueryService;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private UpdateNotificationRepository updateNotificationRepository;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private CommentRepository commentRepository;

  private UpdateNotification 이벤트_알림;
  private UpdateNotification 댓글_알림;
  private Member member;
  private Event event;
  private Comment comment;

  @BeforeEach
  void setUp() {
    member = memberRepository.findById(1L).get();

    event = eventRepository.save(EventFixture.인프콘_2023());

    이벤트_알림 = updateNotificationRepository.save(
        new UpdateNotification(
            member.getId(),
            event.getId(),
            UpdateNotificationType.EVENT,
            LocalDateTime.now()
        )
    );

    comment = commentRepository.save(
        Comment.createRoot(event, member, "내용")
    );

    댓글_알림 = updateNotificationRepository.save(
        new UpdateNotification(
            member.getId(),
            comment.getId(),
            UpdateNotificationType.COMMENT,
            LocalDateTime.now()
        )
    );
  }

  @Test
  @DisplayName("findAll() : 현재 사용자가 받은 댓글 & 행사 알림들을 모두 조회할 수 있다.")
  void test_findAll() throws Exception {
    //given
    final List<UpdateNotificationResponse> expected = List.of(
        new UpdateNotificationResponse(
            이벤트_알림.getId(), member.getId(),
            이벤트_알림.getRedirectId(), LocalDateTime.now(),
            UpdateNotificationType.EVENT, false,
            null),
        new UpdateNotificationResponse(
            댓글_알림.getId(), member.getId(),
            댓글_알림.getRedirectId(), LocalDateTime.now(),
            UpdateNotificationType.COMMENT, false,
            new CommentTypeNotification(
                comment.getContent(),
                comment.getEvent().getName(),
                comment.getMember().getImageUrl(),
                comment.getParentIdOrSelfId(),
                comment.getEvent().getId()
            )
        )
    );

    //when
    final List<UpdateNotificationResponse> actual =
        updateNotificationQueryService.findAll(member, member.getId());

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("createdAt")
        .isEqualTo(expected);
  }
}
