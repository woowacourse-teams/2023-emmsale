package com.emmsale.member.domain;

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
  private Member 사용자;

  @BeforeEach
  void setUp() {
    백엔드 = tagRepository.save(TagFixture.백엔드());
    안드로이드 = tagRepository.save(TagFixture.안드로이드());
    사용자 = memberRepository.findById(1L).get();
  }

  @Test
  @DisplayName("사용자의 모든 관심 태그를 삭제한다.")
  void deleteAllByMemberTest() {
    //given
    final List<InterestTag> interestTags = List.of(new InterestTag(사용자, 백엔드),
        new InterestTag(사용자, 안드로이드));
    interestTagRepository.saveAll(interestTags);

    //when
    interestTagRepository.deleteAllByMember(사용자);

    //then
    final int expectEmptySize = 0;
    final int actualSize = interestTagRepository.findInterestTagsByMemberId(사용자.getId()).size();

    assertEquals(expectEmptySize, actualSize);
  }
}
