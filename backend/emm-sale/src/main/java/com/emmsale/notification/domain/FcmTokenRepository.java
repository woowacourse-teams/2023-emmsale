package com.emmsale.notification.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

  Optional<FcmToken> findByMemberId(final Long memberId);
}
