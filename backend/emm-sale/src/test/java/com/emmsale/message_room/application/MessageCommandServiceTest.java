package com.emmsale.message_room.application;

import static com.emmsale.member.MemberFixture.memberFixture;
import static com.emmsale.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;
import static com.emmsale.message_room.exception.MessageRoomExceptionType.SENDER_IS_NOT_EQUAL_REQUEST_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.emmsale.event_publisher.MessageNotificationEvent;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.member.exception.MemberException;
import com.emmsale.message_room.application.dto.MessageSendRequest;
import com.emmsale.message_room.domain.Message;
import com.emmsale.message_room.domain.MessageRepository;
import com.emmsale.message_room.exception.MessageRoomException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MessageCommandServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private MessageCommandService messageCommandService;
  //  @Autowired
//  private RoomRepository roomRepository;
  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private MemberRepository memberRepository;

  @Nested
  @DisplayName("sendMessage() : 다른 멤버에게 메시지를 보낼 수 있다.")
  class SendMessage {

    //TODO : 채팅방 조회하는 기능이 만들어 진 후에 기능 개발
//    @Test
//    @DisplayName("이미 채팅방(Room)이 존재하는 경우 기존 채팅방에 메시지를 보낸다.")
//    void alreadyRoomExist() {
//      //given
//      final Member member = memberRepository.save(memberFixture());
//      final Long requesterId = member.getId();
//      final Long receiverId = 2L;
//      final String content = "메시지 내용";
//      final Room room = roomRepository.save(
//          new Room(requesterId, LocalDateTime.now(), receiverId, LocalDateTime.now())
//      );
//      final MessageSendRequest request
//          = new MessageSendRequest(requesterId, receiverId, content);
//      final Message expected = new Message(content, requesterId, room, LocalDateTime.now());
//
//      //when
//      messageCommandService.sendMessage(request, member);
//      //then
//      //TODO : room을 조회해서 검증
//      assertThat(messageRepository.findAll())
//          .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id", "createdAt")
//          .containsExactly(expected);
//    }

    @Test
    @DisplayName("채팅방이 존재하지 않는 경우, 새로운 채팅방을 만들고, 메시지를 보낸다.")
    void notExistRoom() {
      //given
      final Member member = memberRepository.save(memberFixture());
      final Long requesterId = member.getId();
      final Long receiverId = 2L;
      final String content = "메시지 내용";
      final MessageSendRequest request
          = new MessageSendRequest(requesterId, receiverId, content);
      final Message expected = new Message(content, requesterId, null, LocalDateTime.now());

      //when
      messageCommandService.sendMessage(request, member);

      //then
      assertAll(
          () -> assertThat(messageRepository.findAll())
              .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id", "createdAt",
                  "roomId")
              .containsExactly(expected),
          () -> verify(firebaseCloudMessageClient, times(1))
              .sendMessageTo(any(MessageNotificationEvent.class))
      );
    }

    @Test
    @DisplayName("member와 sender의 id가 달라서 Exception 발생")
    void failMemberNotEqualSender() {
      final Member member = memberRepository.save(memberFixture());
      final Long requesterId = member.getId() + 1;
      final Long receiverId = 2L;
      final String content = "메시지 내용";
      final MessageSendRequest request
          = new MessageSendRequest(requesterId, receiverId, content);

      //when && then
      assertThatThrownBy(() -> messageCommandService.sendMessage(request, member))
          .isInstanceOf(MessageRoomException.class)
          .hasMessage(SENDER_IS_NOT_EQUAL_REQUEST_MEMBER.errorMessage());
    }

    @Test
    @DisplayName("receiverId에 해당하는 멤버가 존재하지 않을 때 Exception 발생")
    void senderNotFound() {
      final Member member = memberRepository.save(memberFixture());
      final Long requesterId = member.getId();
      final Long receiverId = -1L;
      final String content = "메시지 내용";
      final MessageSendRequest request
          = new MessageSendRequest(requesterId, receiverId, content);

      //when && then
      assertThatThrownBy(() -> messageCommandService.sendMessage(request, member))
          .isInstanceOf(MemberException.class)
          .hasMessage(NOT_FOUND_MEMBER.errorMessage());
    }
  }
}
