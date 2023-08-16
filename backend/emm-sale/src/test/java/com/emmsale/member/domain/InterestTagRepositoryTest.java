package com.emmsale.member.domain;

import static com.emmsale.tag.TagFixture.IOS;
import static com.emmsale.tag.TagFixture.백엔드;
import static com.emmsale.tag.TagFixture.프론트엔드;
import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.tag.domain.Tag;
import com.emmsale.tag.domain.TagRepository;
import java.util.List;
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

  @Test
  @DisplayName("findInterestTagsByTagIdIn() : 주어진 tag id들을 통해 interestTag를 찾을 수 있다.")
  void test_findInterestTagsByTagIdIn() throws Exception {
    //given
    final Member member1 = memberRepository.findById(1L).get();
    final Member member2 = memberRepository.findById(2L).get();

    final Tag tag1 = tagRepository.save(백엔드());
    final Tag tag2 = tagRepository.save(프론트엔드());
    final Tag tag3 = tagRepository.save(IOS());

    //member1 -> tag1, tag2
    //member2 -> tag1, tag2, tag3
    final InterestTag interestTag1 = interestTagRepository.save(new InterestTag(member1, tag1));
    final InterestTag interestTag2 = interestTagRepository.save(new InterestTag(member1, tag2));
    final InterestTag interestTag3 = interestTagRepository.save(new InterestTag(member2, tag1));
    final InterestTag interestTag4 = interestTagRepository.save(new InterestTag(member2, tag2));
    interestTagRepository.save(new InterestTag(member2, tag3));

    final List<InterestTag> expected = List.of(
        interestTag1,
        interestTag2,
        interestTag3,
        interestTag4
    );

    //when
    final List<InterestTag> actual = interestTagRepository
        .findInterestTagsByTagIdIn(List.of(tag1.getId(), tag2.getId()));

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("createdAt")
        .isEqualTo(expected);
  }
}
