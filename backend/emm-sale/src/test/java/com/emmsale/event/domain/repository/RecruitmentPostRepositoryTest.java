package com.emmsale.event.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.event.EventFixture;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.RecruitmentPost;
import com.emmsale.helper.JpaRepositorySliceTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RecruitmentPostRepositoryTest extends JpaRepositorySliceTestHelper {

  @Autowired
  private RecruitmentPostRepository recruitmentPostRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private EventRepository eventRepository;

  private Member 멤버;
  private Event 인프콘;
  private Event 구름톤;

  @BeforeEach
  void setUp() {
    멤버 = memberRepository.findById(1L).get();
    인프콘 = eventRepository.save(EventFixture.인프콘_2023());
    구름톤 = eventRepository.save(EventFixture.구름톤());
  }

  @Test
  @DisplayName("사용자의 모든 함께가기 요청을 조회할 수 있다.")
  void findAllByMemberTest() {
    //given
    final RecruitmentPost post1 = recruitmentPostRepository.save(
        new RecruitmentPost(멤버, 인프콘, "함께가요~"));
    final RecruitmentPost post2 = recruitmentPostRepository.save(
        new RecruitmentPost(멤버, 구름톤, "같이 가요~"));

    final List<RecruitmentPost> expectPosts = List.of(post1, post2);

    //when
    final List<RecruitmentPost> actualPosts = recruitmentPostRepository.findAllByMember(멤버);

    //then
    assertThat(actualPosts)
        .usingRecursiveComparison()
        .isEqualTo(expectPosts);
  }

  @Nested
  @DisplayName("eventId와 memberId로 참가게시글이 존재하는 지 확인할 수 있다.")
  class ExistsByEventIdAndMemberId {

    @Test
    @DisplayName("참가게시글이 존재하는 경우 true를 반환한다.")
    void isExistThenTrue() {
      //given
      recruitmentPostRepository.save(new RecruitmentPost(멤버, 인프콘, "빈 문자열"));

      //when
      final Boolean actual = recruitmentPostRepository.existsByEventIdAndMemberId(인프콘.getId(),
          멤버.getId());

      //then
      assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("Particpant가 존재하지 않는 false를 반환한다.")
    void isNotExistThenFalse() {
      //when
      final Boolean actual = recruitmentPostRepository.existsByEventIdAndMemberId(
          인프콘.getId(),
          멤버.getId());

      //then
      assertThat(actual).isFalse();
    }
  }
}
