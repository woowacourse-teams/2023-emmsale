package com.emmsale.message_room.api;

import com.emmsale.member.domain.Member;
import com.emmsale.message_room.application.RoomQueryService;
import com.emmsale.message_room.application.dto.RoomResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
}
