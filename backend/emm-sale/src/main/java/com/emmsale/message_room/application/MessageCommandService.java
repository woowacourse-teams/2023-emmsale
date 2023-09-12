package com.emmsale.message_room.application;

import static com.emmsale.message_room.exception.MessageExceptionType.SENDER_IS_NOT_EQUAL_REQUEST_MEMBER;

import com.emmsale.event_publisher.MessageNotificationEvent;
import com.emmsale.member.domain.Member;
import com.emmsale.message_room.application.dto.MessageSendRequest;
import com.emmsale.message_room.domain.Message;
import com.emmsale.message_room.domain.MessageRepository;
import com.emmsale.message_room.domain.Room;
import com.emmsale.message_room.domain.RoomId;
import com.emmsale.message_room.domain.RoomRepository;
import com.emmsale.message_room.exception.MessageException;
import com.emmsale.notification.application.NotificationEventListener;
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
  private final NotificationEventListener notificationEventListener;

  public void sendMessage(final MessageSendRequest request, final Member member) {
    //TODO: Room을 조회하는 기능 완성 후, 이미 있는 Room 조회해서 메시지보내는 기능 추가
    if (member.isNotMe(request.getSenderId())) {
      throw new MessageException(SENDER_IS_NOT_EQUAL_REQUEST_MEMBER);
    }

    final String roomId = UUID.randomUUID().toString();
    saveRooms(request, roomId);

    final Message message = new Message(request.getContent(), request.getSenderId(),
        roomId, LocalDateTime.now());
    messageRepository.save(message);
    final MessageNotificationEvent messageNotificationEvent =
        MessageNotificationEvent.of(message, request.getReceiverId());
    notificationEventListener.createMessageNotification(messageNotificationEvent);
  }

  private void saveRooms(final MessageSendRequest request, final String roomId) {
    final RoomId senderRoomId = new RoomId(roomId, request.getSenderId());
    final RoomId receiverRoomId = new RoomId(roomId, request.getReceiverId());

    final Room senderRoom = new Room(senderRoomId, LocalDateTime.now());
    final Room receiverRoom = new Room(receiverRoomId, LocalDateTime.now());

    roomRepository.save(senderRoom);
    roomRepository.save(receiverRoom);
  }
}
