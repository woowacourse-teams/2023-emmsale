package com.emmsale.notification.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  @Query("select n from Notification n where n.receiverId = :receiverId")
  List<Notification> findAllByReceiverId(@Param("receiverId") final Long receiverId);
}
