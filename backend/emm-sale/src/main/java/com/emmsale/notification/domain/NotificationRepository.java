package com.emmsale.notification.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  @Query(value = "select * from notification n "
      + "where json_extract(json_data, '$.receiverId') = :receiverId", nativeQuery = true)
  List<Notification> findAllByReceiverId(@Param("receiverId") final Long receiverId);
}
