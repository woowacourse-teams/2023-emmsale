package com.emmsale.message_room.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.helper.ServiceIntegrationTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.message_room.application.dto.RoomResponse;
import com.emmsale.message_room.domain.Message;
import com.emmsale.message_room.domain.MessageRepository;
import com.emmsale.message_room.domain.Room;
import com.emmsale.message_room.domain.RoomId;
import com.emmsale.message_room.domain.RoomRepository;
import com.emmsale.message_room.infrastructure.persistence.dto.MessageOverview;
import java.time.LocalDateTime;
import java.util.List;
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

  @Test
  @DisplayName("findAll() : 사용자가 중도에 나가는 Room에 상관없이 참여하고 있는 Room 중에서 가장 최근에 받은 메시지들을 조회할 수 있다.")
  void test_findAll() throws Exception {
    final Member loginMember = memberRepository.findById(1L).get();
    loginMember.updateName("member1");
    memberRepository.save(loginMember);

    final Member room1Interlocutor = new Member(3L, "image", "username3");
    room1Interlocutor.updateName("room1Interlocutor");
    memberRepository.save(room1Interlocutor);

    final Member room2Interlocutor = new Member(4L, "image", "username4");
    room2Interlocutor.updateName("room2Interlocutor");
    memberRepository.save(room2Interlocutor);

    final Member room3Interlocutor = new Member(5L, "image", "username5");
    room3Interlocutor.updateName("room3Interlocutor");
    memberRepository.save(room3Interlocutor);

    final String room1UUID = "feed014c-33f7-418c-8841-5553db5f22c1";
    final String room2UUID = "feed014c-33f7-418c-8841-5553db5f22c2";
    final String room3UUID = "feed014c-33f7-418c-8841-5553db5f22c3";

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

    final Message resultMessage1 = new Message(
        "방1메시지3",
        loginMember.getId(),
        room1UUID,
        LocalDateTime.parse("2023-10-07T16:45:39")
    );
    final Message resultMessage2 = new Message(
        "방2메시지4",
        room2Interlocutor.getId(),
        room2UUID,
        LocalDateTime.parse("2023-10-07T16:45:39")
    );

    messageRepository.saveAll(
        List.of(
            new Message("방1메시지1", loginMember.getId(), room1UUID,
                LocalDateTime.parse("2023-05-07T16:45:39")),
            new Message("방1메시지2", loginMember.getId(), room1UUID,
                LocalDateTime.parse("2023-06-07T16:45:39")),
            resultMessage1,
            resultMessage2,
            new Message("방3메시지5", loginMember.getId(), room3UUID,
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

    final List<RoomResponse> actual = List.of(
        RoomResponse.from(messageOverview1,
            memberRepository.findById(room1Interlocutor.getId()).get()),
        RoomResponse.from(messageOverview2,
            memberRepository.findById(room2Interlocutor.getId()).get()
        ));

    //when
    final List<RoomResponse> expect = roomQueryService.findAll(loginMember, loginMember.getId());

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);
  }
}
