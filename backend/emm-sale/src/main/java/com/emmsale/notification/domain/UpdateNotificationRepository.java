package com.emmsale.notification.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UpdateNotificationRepository extends JpaRepository<UpdateNotification, Long> {

  List<UpdateNotification> findAllByReceiverId(final Long receiverId);

  List<UpdateNotification> findAllByIdIn(final List<Long> notificationIds);

  @Modifying
  @Query("delete from UpdateNotification un where un.id in :ids")
  void deleteBatchByIdsIn(@Param("ids") final List<Long> notificationIds);
}
