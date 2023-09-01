package com.emmsale.message_room.application;

import static com.emmsale.message_room.exception.MessageExceptionType.SENDER_IS_NOT_EQUAL_REQUEST_MEMBER;

import com.emmsale.member.domain.Member;
import com.emmsale.message_room.application.dto.MessageSendRequest;
import com.emmsale.message_room.domain.Message;
import com.emmsale.message_room.domain.MessageRepository;
import com.emmsale.message_room.domain.Room;
import com.emmsale.message_room.domain.RoomRepository;
import com.emmsale.message_room.exception.MessageException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageCommandService {

  private final MessageRepository messageRepository;
  private final RoomRepository roomRepository;

  public void sendMessage(final MessageSendRequest request, final Member member) {
    //TODO : room 조회하는 기능 완성 후 추가
    if (member.isNotMe(request.getSenderId())) {
      throw new MessageException(SENDER_IS_NOT_EQUAL_REQUEST_MEMBER);
    }
    final Room room = new Room(
        request.getSenderId(),
        LocalDateTime.now(),
        request.getReceiverId(),
        LocalDateTime.now()
    );
    final Message message = new Message(request.getContent(), request.getSenderId(),
        LocalDateTime.now());
    room.addMessage(message);
    roomRepository.save(room);
  }
}
