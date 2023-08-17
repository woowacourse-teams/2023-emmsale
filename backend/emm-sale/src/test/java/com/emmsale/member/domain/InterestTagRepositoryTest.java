package com.emmsale.member.domain;

import static com.emmsale.tag.TagFixture.IOS;
import static com.emmsale.tag.TagFixture.프론트엔드;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.emmsale.tag.TagFixture;
import com.emmsale.tag.domain.Tag;
import com.emmsale.tag.domain.TagRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Sql("/data-test.sql")
class InterestTagRepositoryTest {

  @Autowired
  private InterestTagRepository interestTagRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private TagRepository tagRepository;

  private Tag 백엔드;
  private Tag 안드로이드;
  private Tag 프론트엔드;
  private Tag IOS;
  private Member 사용자1;
  private Member 사용자2;

  @BeforeEach
  void setUp() {
    백엔드 = tagRepository.save(TagFixture.백엔드());
    안드로이드 = tagRepository.save(TagFixture.안드로이드());
    프론트엔드 = tagRepository.save(프론트엔드());
    IOS = tagRepository.save(IOS());
    사용자1 = memberRepository.findById(1L).get();
    사용자2 = memberRepository.findById(2L).get();
  }

  @Test
  @DisplayName("findInterestTagsByTagIdIn() : 주어진 tag id들을 통해 interestTag를 찾을 수 있다.")
  void test_findInterestTagsByTagIdIn() throws Exception {
    //given

    //사용자1 -> 백엔드, 프론트엔드
    //사용자2 -> 백엔드, 프론트엔드, IOS
    final InterestTag interestTag1 = interestTagRepository.save(new InterestTag(사용자1, 백엔드));
    final InterestTag interestTag2 = interestTagRepository.save(new InterestTag(사용자1, 프론트엔드));
    final InterestTag interestTag3 = interestTagRepository.save(new InterestTag(사용자2, 백엔드));
    final InterestTag interestTag4 = interestTagRepository.save(new InterestTag(사용자2, 프론트엔드));
    interestTagRepository.save(new InterestTag(사용자2, IOS));

    final List<InterestTag> expected = List.of(
        interestTag1,
        interestTag2,
        interestTag3,
        interestTag4
    );

    //when
    final List<InterestTag> actual = interestTagRepository
        .findInterestTagsByTagIdIn(List.of(백엔드.getId(), 프론트엔드.getId()));

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("createdAt")
        .isEqualTo(expected);
  }

  @Test
  @DisplayName("사용자의 모든 관심 태그를 삭제한다.")
  void deleteAllByMemberTest() {
    //given
    final List<InterestTag> interestTags = List.of(new InterestTag(사용자1, 백엔드),
        new InterestTag(사용자1, 안드로이드));
    interestTagRepository.saveAll(interestTags);

    //when
    interestTagRepository.deleteAllByMember(사용자1);

    //then
    final int expectEmptySize = 0;
    final int actualSize = interestTagRepository.findInterestTagsByMemberId(사용자1.getId()).size();

    assertEquals(expectEmptySize, actualSize);
  }
}
