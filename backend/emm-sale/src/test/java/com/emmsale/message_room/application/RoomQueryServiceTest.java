package com.emmsale.message_room.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.emmsale.fixture.MessageFixture;
import com.emmsale.fixture.RoomFixture;
import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.MemberFixture;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.message_room.application.dto.MessageResponse;
import com.emmsale.message_room.application.dto.RoomResponse;
import com.emmsale.message_room.domain.Message;
import com.emmsale.message_room.domain.MessageRepository;
import com.emmsale.message_room.domain.RoomRepository;
import com.emmsale.message_room.exception.MessageRoomException;
import com.emmsale.message_room.exception.MessageRoomExceptionType;
import com.emmsale.message_room.infrastructure.persistence.dto.MessageOverview;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RoomQueryServiceTest extends ServiceIntegrationTestHelper {

  @Autowired
  private RoomQueryService roomQueryService;
  @Autowired
  private RoomRepository roomRepository;
  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private MemberRepository memberRepository;

  private String room1UUID, room2UUID, room3UUID;
  private Member loginMember, room1Interlocutor, room2Interlocutor, room3Interlocutor;

  @BeforeEach
  void init() {
    room1UUID = "feed014c-33f7-418c-8841-5553db5f22c1";
    room2UUID = "feed014c-33f7-418c-8841-5553db5f22c2";
    room3UUID = "feed014c-33f7-418c-8841-5553db5f22c3";

    loginMember = memberRepository.findById(1L).get();
    loginMember.updateName("member1");
    memberRepository.save(loginMember);

    room1Interlocutor = MemberFixture.create(3L, "username3", "room1Interlocutor");
    memberRepository.save(room1Interlocutor);

    room2Interlocutor = MemberFixture.create(4L, "username4", "room2Interlocutor");
    memberRepository.save(room2Interlocutor);

    room3Interlocutor = MemberFixture.create(5L, "username5", "room3Interlocutor");
    memberRepository.save(room3Interlocutor);

    roomRepository.saveAll(List.of(
        RoomFixture.create(room1UUID, loginMember.getId(),
            LocalDateTime.parse("2023-09-05T16:26:20.751352")),
        RoomFixture.create(room1UUID, room1Interlocutor.getId(),
            LocalDateTime.parse("2023-09-07T16:48:24")),

        RoomFixture.create(room2UUID, loginMember.getId(),
            LocalDateTime.parse("2023-09-07T16:48:24")),
        RoomFixture.create(room2UUID, room2Interlocutor.getId(),
            LocalDateTime.parse("2023-09-07T16:48:24")),

        RoomFixture.create(room3UUID, loginMember.getId(),
            LocalDateTime.parse("2023-10-07T16:48:24")),
        RoomFixture.create(room3UUID, room3Interlocutor.getId(),
            LocalDateTime.parse("2023-08-07T16:45:39"))
    ));
  }

  @Test
  @DisplayName("findAll() : 사용자가 중도에 나가는 Room에 상관없이 참여하고 있는 Room 중에서 가장 최근에 받은 메시지들을 조회할 수 있다.")
  void test_findAll() throws Exception {
    //given
    final Message resultMessage1 = MessageFixture.create(
        "방1메시지3",
        loginMember.getId(),
        room1UUID,
        LocalDateTime.parse("2023-10-07T16:45:39")
    );
    final Message resultMessage2 = MessageFixture.create(
        "방2메시지4",
        room2Interlocutor.getId(),
        room2UUID,
        LocalDateTime.parse("2023-10-07T16:45:39")
    );

    messageRepository.saveAll(
        List.of(
            MessageFixture.create("방1메시지1", loginMember.getId(), room1UUID,
                LocalDateTime.parse("2023-05-07T16:45:39")),
            MessageFixture.create("방1메시지2", loginMember.getId(), room1UUID,
                LocalDateTime.parse("2023-06-07T16:45:39")),
            resultMessage1,
            resultMessage2,
            MessageFixture.create("방3메시지5", loginMember.getId(), room3UUID,
                LocalDateTime.parse("2023-08-07T16:45:39"))
        )
    );

    final MessageOverview messageOverview1 = new MessageOverview(
        resultMessage1.getId(),
        resultMessage1.getContent(),
        resultMessage1.getCreatedAt(),
        resultMessage1.getRoomId()
    );

    final MessageOverview messageOverview2 = new MessageOverview(
        resultMessage2.getId(),
        resultMessage2.getContent(),
        resultMessage2.getCreatedAt(),
        resultMessage2.getRoomId()
    );

    final List<RoomResponse> expect = List.of(
        RoomResponse.from(messageOverview1,
            memberRepository.findById(room1Interlocutor.getId()).get()),
        RoomResponse.from(messageOverview2,
            memberRepository.findById(room2Interlocutor.getId()).get())
    );

    //when
    final List<RoomResponse> actual = roomQueryService.findAll(loginMember, loginMember.getId());

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);
  }

  @Test
  @DisplayName("findByRoomId() : Room UUID를 통해 Room에 있는 메시지들을 조회할 수 있다.")
  void test_findByRoomId() throws Exception {
    //given
    final Message room1Message1 = new Message("방1메시지1", loginMember.getId(), room1UUID,
        LocalDateTime.parse("2023-05-07T16:45:39"));
    final Message room1Message2 = new Message("방1메시지2", loginMember.getId(), room1UUID,
        LocalDateTime.parse("2023-06-07T16:45:38"));

    messageRepository.saveAll(
        List.of(
            room1Message1,
            room1Message2,
            new Message("방2메시지3", loginMember.getId(), room2UUID,
                LocalDateTime.parse("2023-10-07T16:45:39")),
            new Message("방2메시지4", room1Interlocutor.getId(), room2UUID,
                LocalDateTime.parse("2023-10-07T16:45:39"))
        )
    );

    final List<MessageResponse> expect = List.of(
        new MessageResponse(room1Message1.getSenderId(), room1Message1.getContent(),
            room1Message1.getCreatedAt()),
        new MessageResponse(room1Message2.getSenderId(), room1Message2.getContent(),
            room1Message2.getCreatedAt())
    );

    //when
    final List<MessageResponse> actual = roomQueryService.findByRoomId(loginMember, room1UUID,
        loginMember.getId());

    //then
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);
  }

  @Test
  @DisplayName("findByInterlocutorIds() : Room 에 참여한 사용자의 ID를 통해 쪽지방을 조회할 수 있다.")
  void test_findByInterlocutorIds() throws Exception {
    //given
    final Message room1Message1 = new Message("방1메시지1", loginMember.getId(), room1UUID,
        LocalDateTime.parse("2023-05-07T16:45:39"));
    final Message room1Message2 = new Message("방1메시지2", loginMember.getId(), room1UUID,
        LocalDateTime.parse("2023-06-07T16:45:38"));

    messageRepository.saveAll(
        List.of(
            room1Message1,
            room1Message2,
            new Message("방2메시지3", loginMember.getId(), room2UUID,
                LocalDateTime.parse("2023-10-07T16:45:39")),
            new Message("방2메시지4", room1Interlocutor.getId(), room2UUID,
                LocalDateTime.parse("2023-10-07T16:45:39"))
        )
    );

    final List<MessageResponse> expect = List.of(
        new MessageResponse(room1Message1.getSenderId(), room1Message1.getContent(),
            room1Message1.getCreatedAt()),
        new MessageResponse(room1Message2.getSenderId(), room1Message2.getContent(),
            room1Message2.getCreatedAt())
    );

    //when
    final List<MessageResponse> actual = roomQueryService.findByInterlocutorIds(
        loginMember.getId(),
        room1Interlocutor.getId(),
        loginMember.getId(),
        loginMember
    );

    //then
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);
  }

  @Test
  @DisplayName("findByInterlocutorIds() : Room 에 참여한 사용자의 ID를 통해 쪽지방을 조회할 수 있다.")
  void test_findByInterlocutorIds_() throws Exception {
    //given
    final Message room1Message1 = new Message("방1메시지1", loginMember.getId(), room1UUID,
        LocalDateTime.parse("2023-05-07T16:45:39"));
    final Message room1Message2 = new Message("방1메시지2", loginMember.getId(), room1UUID,
        LocalDateTime.parse("2023-06-07T16:45:38"));

    messageRepository.saveAll(
        List.of(
            room1Message1,
            room1Message2,
            new Message("방2메시지3", loginMember.getId(), room2UUID,
                LocalDateTime.parse("2023-10-07T16:45:39")),
            new Message("방2메시지4", room1Interlocutor.getId(), room2UUID,
                LocalDateTime.parse("2023-10-07T16:45:39"))
        )
    );

    //when & then
    assertThatThrownBy(
        () -> roomQueryService.findByInterlocutorIds(
            room2Interlocutor.getId(), room1Interlocutor.getId(), loginMember.getId(), loginMember))
        .isInstanceOf(MessageRoomException.class)
        .hasMessage(MessageRoomExceptionType.FORBIDDEN_NOT_INTERLOCUTORS.errorMessage());
  }
}
