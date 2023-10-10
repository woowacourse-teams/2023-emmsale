package com.emmsale.notification.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  @Query("select n from Notification n where n.receiverId = :receiverId")
  List<Notification> findAllByReceiverId(@Param("receiverId") final Long receiverId);

  List<Notification> findAllByIdIn(final List<Long> notificationIds);

  @Modifying
  @Query("delete from Notification n where n.id in :ids")
  void deleteBatchByIdsIn(@Param("ids") final List<Long> notificationIds);
}
