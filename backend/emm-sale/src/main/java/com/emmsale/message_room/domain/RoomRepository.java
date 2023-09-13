package com.emmsale.message_room.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Room, RoomId> {

  @Query("select r from Room r where r.roomId.uuid in :uuids")
  List<Room> findAllByUUIDsIn(final List<String> uuids);

  @Query("select r1 from Room r1 "
      + "inner join Room r2 "
      + "on r1.roomId.uuid = r2.roomId.uuid "
      + "where r1.roomId.memberId = :member1 and r2.roomId.memberId = :member2")
  Optional<Room> findByInterlocutorIds(
      @Param("member1") final Long interlocutorId1,
      @Param("member2") final Long interlocutorId2
  );
}
