package com.emmsale.admin.report.application;


import static com.emmsale.event.EventFixture.eventFixture;
import static com.emmsale.member.MemberFixture.adminMember;
import static com.emmsale.member.MemberFixture.generalMember;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.emmsale.comment.domain.Comment;
import com.emmsale.comment.domain.CommentRepository;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.feed.domain.Feed;
import com.emmsale.feed.domain.repository.FeedRepository;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.login.exception.LoginException;
import com.emmsale.login.exception.LoginExceptionType;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.report.application.ReportCommandService;
import com.emmsale.report.application.dto.ReportCreateRequest;
import com.emmsale.report.application.dto.ReportCreateResponse;
import com.emmsale.report.application.dto.ReportFindResponse;
import com.emmsale.report.domain.ReportType;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReportQueryServiceTest extends ServiceIntegrationTestHelper {

  private static Long 신고자_ID;
  private static Long 신고_대상자_ID;
  @Autowired
  private ReportQueryService reportQueryService;
  @Autowired
  private ReportCommandService reportCommandService;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private FeedRepository feedRepository;
  @Autowired
  private CommentRepository commentRepository;

  @BeforeEach
  void init() {
    final Event event = eventRepository.save(eventFixture());
    final Member 신고자 = memberRepository.findById(1L).get();
    final Member 신고_대상자 = memberRepository.findById(2L).get();
    final Member feedWriter = memberRepository.save(new Member(111L, "img-url", "uname"));
    final Feed feed = feedRepository.save(new Feed(event, feedWriter, "피드 제목", "피드 내용"));
    신고자_ID = 신고자.getId();
    신고_대상자_ID = 신고_대상자.getId();
    commentRepository.save(Comment.createRoot(feed, 신고_대상자, "상대방에게 불쾌감을 줄 수 있는 내용"));
    commentRepository.save(Comment.createRoot(feed, 신고자, "그냥 댓글"));
  }

  @Test
  @DisplayName("모든 신고 목록을 조회할 수 있다.")
  void findReports() {
    // given
    final Long abusingContentId = 1L;
    final Member reporter = memberRepository.findById(신고자_ID).get();
    final ReportCreateRequest request = new ReportCreateRequest(신고자_ID, 신고_대상자_ID,
        ReportType.COMMENT,
        abusingContentId);
    final ReportCreateResponse report = reportCommandService.create(request, reporter);
    final List<ReportFindResponse> expected = List.of(
        new ReportFindResponse(report.getId(), report.getReporterId(), report.getReportedId(),
            report.getType(), report.getContentId(), report.getCreatedAt()));

    // when
    final List<ReportFindResponse> actual = reportQueryService.findReports(adminMember());

    // then
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("createdAt")
        .isEqualTo(expected);
  }

  @Test
  @DisplayName("관리자가 아닌 회원이 신고 목록을 조회하면 예외를 반환한다.")
  void findReports_fail_authorization() {
    // given, when
    final ThrowingCallable actual = () -> reportQueryService.findReports(generalMember());

    // then
    assertThatThrownBy(actual)
        .isInstanceOf(LoginException.class)
        .hasMessage(LoginExceptionType.INVALID_ACCESS_TOKEN.errorMessage());
  }
}
