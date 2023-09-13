package com.emmsale.message_room.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.emmsale.helper.JpaRepositorySliceTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MessageRepositoryTest extends JpaRepositorySliceTestHelper {

  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private MemberRepository memberRepository;

  @Test
  @DisplayName("findByRoomId() : Room UUID를 통해 Room에 있는 메시지들을 조회할 수 있다.")
  void test_findByRoomId() throws Exception {
    //given
    final Member loginMember = memberRepository.findById(1L).get();
    final Member room1Interlocutor = memberRepository.findById(2L).get();
    final Member room2Interlocutor = memberRepository.save(new Member(333L, "image", "usdafkl"));

    final String room1UUID = "feed014c-33f7-418c-8841-5553db5f22c1";
    final String room2UUID = "feed014c-33f7-418c-8841-5553db5f22c2";

    final Room member1Room1 = new Room(new RoomId(room1UUID, loginMember.getId()),
        LocalDateTime.parse("2023-09-05T16:26:20.751352"));
    final Room member2Room1 = new Room(new RoomId(room1UUID, room1Interlocutor.getId()),
        LocalDateTime.parse("2023-09-07T16:48:24"));
    final Room member1Room2 = new Room(new RoomId(room2UUID, loginMember.getId()),
        LocalDateTime.parse("2023-09-07T16:48:24"));
    final Room member3Room2 = new Room(new RoomId(room2UUID, room2Interlocutor.getId()),
        LocalDateTime.parse("2023-09-07T16:48:24"));

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

    final List<Message> expect = List.of(room1Message1, room1Message2);

    //when
    List<Message> actual = messageRepository.findByRoomUUID(room1UUID);

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);
  }
}
