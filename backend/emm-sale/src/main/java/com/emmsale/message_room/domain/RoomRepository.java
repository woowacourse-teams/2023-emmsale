package com.emmsale.message_room.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, RoomId> {

}
