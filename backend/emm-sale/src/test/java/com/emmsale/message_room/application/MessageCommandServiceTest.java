package com.emmsale.message_room.application;

import static com.emmsale.member.MemberFixture.memberFixture;
import static com.emmsale.message_room.exception.MessageExceptionType.SENDER_IS_NOT_EQUAL_REQUEST_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.message_room.application.dto.MessageSendRequest;
import com.emmsale.message_room.domain.Message;
import com.emmsale.message_room.domain.MessageRepository;
import com.emmsale.message_room.domain.RoomRepository;
import com.emmsale.message_room.exception.MessageException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MessageCommandServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private MessageCommandService messageCommandService;
  @Autowired
  private RoomRepository roomRepository;
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
      final Message expected = new Message(content, requesterId, LocalDateTime.now());

      //when
      messageCommandService.sendMessage(request, member);
      //then
      //TODO : room을 조회해서 검증
      assertThat(messageRepository.findAll())
          .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id", "createdAt", "room")
          .containsExactly(expected);
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
          .isInstanceOf(MessageException.class)
          .hasMessage(SENDER_IS_NOT_EQUAL_REQUEST_MEMBER.errorMessage());
    }
  }
}
