package com.emmsale.message_room.application;

import static com.emmsale.message_room.exception.MessageRoomExceptionType.SENDER_IS_NOT_EQUAL_REQUEST_MEMBER;

import com.emmsale.event_publisher.EventPublisher;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.member.exception.MemberException;
import com.emmsale.member.exception.MemberExceptionType;
import com.emmsale.message_room.application.dto.MessageSendRequest;
import com.emmsale.message_room.application.dto.MessageSendResponse;
import com.emmsale.message_room.domain.Message;
import com.emmsale.message_room.domain.MessageRepository;
import com.emmsale.message_room.domain.Room;
import com.emmsale.message_room.domain.RoomId;
import com.emmsale.message_room.domain.RoomRepository;
import com.emmsale.message_room.exception.MessageRoomException;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageCommandService {

  private final MessageRepository messageRepository;
  private final RoomRepository roomRepository;
  private final EventPublisher eventPublisher;
  private final MemberRepository memberRepository;

  public MessageSendResponse sendMessage(final MessageSendRequest request, final Member member) {
    validateMembers(request, member);

    final String roomId = roomRepository.findByInterlocutorIds(request.getReceiverId(),
            request.getSenderId())
        .map(room -> room.getRoomId().getUuid())
        .orElseGet(() -> saveRooms(request));

    final Message message = new Message(request.getContent(), request.getSenderId(),
        roomId, LocalDateTime.now());

    messageRepository.save(message);
    eventPublisher.publish(message, request.getReceiverId());
    return new MessageSendResponse(roomId);
  }

  private void validateMembers(final MessageSendRequest request, final Member member) {
    if (member.isNotMe(request.getSenderId())) {
      throw new MessageRoomException(SENDER_IS_NOT_EQUAL_REQUEST_MEMBER);
    }
    if (!memberRepository.existsById(request.getReceiverId())) {
      throw new MemberException(MemberExceptionType.NOT_FOUND_MEMBER);
    }
  }

  private String saveRooms(final MessageSendRequest request) {
    final String roomId = UUID.randomUUID().toString();

    final RoomId senderRoomId = new RoomId(roomId, request.getSenderId());
    final RoomId receiverRoomId = new RoomId(roomId, request.getReceiverId());

    final Room senderRoom = new Room(senderRoomId, LocalDateTime.now());
    final Room receiverRoom = new Room(receiverRoomId, LocalDateTime.now());

    roomRepository.save(senderRoom);
    roomRepository.save(receiverRoom);

    return roomId;
  }
}
