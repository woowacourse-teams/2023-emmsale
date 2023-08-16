package com.emmsale.notification.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestNotificationRepository extends JpaRepository<RequestNotification, Long> {

  List<RequestNotification> findAllByReceiverId(Long memberId);

  boolean existsBySenderIdAndReceiverIdAndEventId(
      final Long senderId, final Long receiverId, final Long eventId
  );

  Optional<RequestNotification> findBySenderIdAndReceiverIdAndEventId(final Long senderId,
      final Long receiverId, final Long eventId);
}
