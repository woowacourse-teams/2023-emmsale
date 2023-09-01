package com.emmsale.message_room.application;

import com.emmsale.member.domain.Member;
import com.emmsale.message_room.application.dto.MessageSendRequest;
import com.emmsale.message_room.domain.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageCommandService {

  private final MessageRepository messageRepository;

  public void sendMessage(final MessageSendRequest request, final Member member) {

  }
}
