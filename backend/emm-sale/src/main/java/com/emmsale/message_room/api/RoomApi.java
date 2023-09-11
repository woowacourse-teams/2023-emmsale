package com.emmsale.message_room.api;

import com.emmsale.member.domain.Member;
import com.emmsale.message_room.application.RoomQueryService;
import com.emmsale.message_room.application.dto.MessageResponse;
import com.emmsale.message_room.application.dto.RoomResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RoomApi {

  private final RoomQueryService roomQueryService;

  @GetMapping("/rooms")
  public List<RoomResponse> findAllRoom(
      final Member loginMember,
      @RequestParam("member-id") final Long memberId
  ) {
    return roomQueryService.findAll(loginMember, memberId);
  }

  @GetMapping("/rooms/{room-id}")
  public List<MessageResponse> findByRoomId(
      final Member member,
      @PathVariable("room-id") final String roomId,
      @RequestParam("member-id") final Long memberId
  ) {
    return roomQueryService.findByRoomId(member, roomId, memberId);
  }
}
