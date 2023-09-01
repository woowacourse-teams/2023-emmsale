package com.emmsale.message_room.api;

import com.emmsale.member.domain.Member;
import com.emmsale.message_room.application.MessageCommandService;
import com.emmsale.message_room.application.dto.MessageSendRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MessagesApi {

  private final MessageCommandService messageCommandService;

  @PostMapping("/messages")
  public ResponseEntity<Void> sendMessage(final MessageSendRequest request, final Member member) {
    messageCommandService.sendMessage(request, member);
    return ResponseEntity.ok().build();
  }
}
