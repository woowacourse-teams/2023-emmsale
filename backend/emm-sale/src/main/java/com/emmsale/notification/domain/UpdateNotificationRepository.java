package com.emmsale.notification.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UpdateNotificationRepository extends JpaRepository<UpdateNotification, Long> {

  List<UpdateNotification> findAllByReceiverId(final Long receiverId);

  List<UpdateNotification> findAllByIdIn(final List<Long> notificationIds);
}
