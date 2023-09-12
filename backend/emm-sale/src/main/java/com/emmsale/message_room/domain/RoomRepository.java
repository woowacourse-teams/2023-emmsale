package com.emmsale.message_room.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoomRepository extends JpaRepository<Room, RoomId> {

  @Query("select r from Room r where r.roomId.uuid = :uuid")
  List<Room> findByUUID(final String uuid);
}
