package com.emmsale.member.domain;

import static com.emmsale.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

import com.emmsale.login.domain.OAuthProviderType;
import com.emmsale.member.exception.MemberException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByOauthId(Long oauthId);

  @Query("select count(*) from Member m where m.id in :ids")
  Long countMembersById(@Param("ids") final List<Long> ids);

  boolean existsById(@NotNull final Long id);

  @Query("select m from Member m where m.id in :memberIds")
  List<Member> findAllByIdIn(@Param("memberIds") final Set<Long> memberIds);

  default Member getByIdOrElseThrow(final Long id) {
    return findById(id).orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
  }

  Optional<Member> findByOauthIdAndOauthProviderType(final Long oauthId,
      final OAuthProviderType oauthProviderType);
}
