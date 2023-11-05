package com.emmsale.message_room.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.MemberFixture;
import com.emmsale.member.application.dto.MemberReferenceResponse;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.message_room.application.dto.MessageResponse;
import com.emmsale.message_room.application.dto.RoomResponse;
import com.emmsale.message_room.domain.Message;
import com.emmsale.message_room.domain.MessageRepository;
import com.emmsale.message_room.domain.Room;
import com.emmsale.message_room.domain.RoomId;
import com.emmsale.message_room.domain.RoomRepository;
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
        new Room(new RoomId(room1UUID, loginMember.getId()),
            LocalDateTime.parse("2023-09-05T16:26:20.751352")),
        new Room(new RoomId(room1UUID, room1Interlocutor.getId()),
            LocalDateTime.parse("2023-09-07T16:48:24")),

        new Room(new RoomId(room2UUID, loginMember.getId()),
            LocalDateTime.parse("2023-09-07T16:48:24")),
        new Room(new RoomId(room2UUID, room2Interlocutor.getId()),
            LocalDateTime.parse("2023-09-07T16:48:24")),

        new Room(new RoomId(room3UUID, loginMember.getId()),
            LocalDateTime.parse("2023-10-07T16:48:24")),
        new Room(new RoomId(room3UUID, room3Interlocutor.getId()),
            LocalDateTime.parse("2023-08-07T16:45:39"))
    ));
  }

  @Test
  @DisplayName("findAll() : 사용자가 중도에 나가는 Room에 상관없이 참여하고 있는 Room 중에서 가장 최근에 받은 메시지들을 조회할 수 있다.")
  void test_findAll() throws Exception {
    //given
    final Message resultMessage1 = new Message(
        "방1메시지3",
        loginMember,
        room1UUID,
        LocalDateTime.parse("2023-10-07T16:45:39")
    );
    final Message resultMessage2 = new Message(
        "방2메시지4",
        room2Interlocutor,
        room2UUID,
        LocalDateTime.parse("2023-10-07T16:45:39")
    );

    messageRepository.saveAll(
        List.of(
            new Message("방1메시지1", loginMember, room1UUID,
                LocalDateTime.parse("2023-05-07T16:45:39")),
            new Message("방1메시지2", loginMember, room1UUID,
                LocalDateTime.parse("2023-06-07T16:45:39")),
            resultMessage1,
            resultMessage2,
            new Message("방3메시지5", loginMember, room3UUID,
                LocalDateTime.parse("2023-08-07T16:45:39"))
        )
    );

    final MessageOverview messageOverview1 = new MessageOverview(
        resultMessage1.getId(),
        resultMessage1.getContent(),
        resultMessage1.getSender().getId(),
        resultMessage1.getCreatedAt(),
        resultMessage1.getRoomId()
    );

    final MessageOverview messageOverview2 = new MessageOverview(
        resultMessage2.getId(),
        resultMessage2.getContent(),
        resultMessage2.getSender().getId(),
        resultMessage2.getCreatedAt(),
        resultMessage2.getRoomId()
    );

    final List<RoomResponse> expect = List.of(
        RoomResponse.from(messageOverview1, resultMessage1.getSender(),
            memberRepository.findById(room1Interlocutor.getId()).get()),
        RoomResponse.from(messageOverview2, resultMessage2.getSender(),
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
    final Message room1Message1 = new Message("방1메시지1", loginMember, room1UUID,
        LocalDateTime.parse("2023-05-07T16:45:39"));
    final Message room1Message2 = new Message("방1메시지2", loginMember, room1UUID,
        LocalDateTime.parse("2023-06-07T16:45:38"));

    messageRepository.saveAll(
        List.of(
            room1Message1,
            room1Message2,
            new Message("방2메시지3", loginMember, room2UUID,
                LocalDateTime.parse("2023-10-07T16:45:39")),
            new Message("방2메시지4", room1Interlocutor, room2UUID,
                LocalDateTime.parse("2023-10-07T16:45:39"))
        )
    );

    final List<MessageResponse> expect = List.of(
        new MessageResponse(room1Message1.getId(),
            MemberReferenceResponse.from(room1Message1.getSender()), room1Message1.getContent(),
            room1Message1.getCreatedAt()),
        new MessageResponse(room1Message2.getId(),
            MemberReferenceResponse.from(room1Message2.getSender()), room1Message2.getContent(),
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
    final Message room1Message1 = new Message("방1메시지1", loginMember, room1UUID,
        LocalDateTime.parse("2023-05-07T16:45:39"));
    final Message room1Message2 = new Message("방1메시지2", loginMember, room1UUID,
        LocalDateTime.parse("2023-06-07T16:45:38"));

    messageRepository.saveAll(
        List.of(
            room1Message1,
            room1Message2,
            new Message("방2메시지3", loginMember, room2UUID,
                LocalDateTime.parse("2023-10-07T16:45:39")),
            new Message("방2메시지4", room1Interlocutor, room2UUID,
                LocalDateTime.parse("2023-10-07T16:45:39"))
        )
    );

    final List<MessageResponse> expect = List.of(
        new MessageResponse(room1Message1.getId(),
            MemberReferenceResponse.from(room1Message1.getSender()), room1Message1.getContent(),
            room1Message1.getCreatedAt()),
        new MessageResponse(room1Message2.getId(),
            MemberReferenceResponse.from(room1Message2.getSender()), room1Message2.getContent(),
            room1Message2.getCreatedAt())
    );

    //when
    final List<MessageResponse> actual = roomQueryService.findByInterlocutorIds(
        room1Interlocutor.getId(),
        loginMember.getId(),
        loginMember
    );

    //then
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);
  }
}
