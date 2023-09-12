package com.emmsale.message_room.domain;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.emmsale.helper.JpaRepositorySliceTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

class RoomRepositoryTest extends JpaRepositorySliceTestHelper {

  @Autowired
  private RoomRepository roomRepository;
  @Autowired
  private MemberRepository memberRepository;

  @Test
  @Rollback(value = false)
  @DisplayName("findAllByUUIDsIn() : UUID들을 통해서 Room들을 조회할 수 있다.")
  void test_findByUUID() throws Exception {
    //given
    final Member member1 = memberRepository.findById(1L).get();
    final Member member2 = memberRepository.findById(2L).get();
    final Member member3 = memberRepository.save(new Member(333L, "image", "usdafkl"));

    final String room1UUID = "feed014c-33f7-418c-8841-5553db5f22c1";
    final String room2UUID = "feed014c-33f7-418c-8841-5553db5f22c2";

    final Room member1Room1 = new Room(new RoomId(room1UUID, member1.getId()),
        LocalDateTime.parse("2023-09-05T16:26:20.751352"));
    final Room member2Room1 = new Room(new RoomId(room1UUID, member2.getId()),
        LocalDateTime.parse("2023-09-07T16:48:24"));
    final Room member1Room2 = new Room(new RoomId(room2UUID, member1.getId()),
        LocalDateTime.parse("2023-09-07T16:48:24"));
    final Room member3Room2 = new Room(new RoomId(room2UUID, member3.getId()),
        LocalDateTime.parse("2023-09-07T16:48:24"));

    roomRepository.saveAll(List.of(member1Room1, member2Room1, member1Room2, member3Room2));

    final List<String> roomUUIDs = List.of(
        member1Room1.getRoomId().getUuid(),
        member1Room2.getRoomId().getUuid()
    );
    final List<Room> actual = List.of(member1Room1, member2Room1, member1Room2, member3Room2);

    //when
    final List<Room> expect = roomRepository.findAllByUUIDsIn(roomUUIDs);

    //then
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);
  }

  @Test
  @DisplayName("findByInterlocutorIds() : 쪽지방 참여자들 ID를 통해 Room을 조회할 수 있다.")
  void test_findByInterlocutorIds() throws Exception {
    //given
    final Member member1 = memberRepository.findById(1L).get();
    final Member member2 = memberRepository.findById(2L).get();

    final String room1UUID = "feed014c-33f7-418c-8841-5553db5f22c1";

    final Room member1Room1 = new Room(new RoomId(room1UUID, member1.getId()),
        LocalDateTime.parse("2023-09-05T16:26:20.751352"));
    final Room member2Room1 = new Room(new RoomId(room1UUID, member2.getId()),
        LocalDateTime.parse("2023-09-07T16:48:24"));

    roomRepository.saveAll(List.of(member1Room1, member2Room1));

    //when
    Optional<Room> room = roomRepository.findByInterlocutorIds(member1.getId(), member2.getId());

    //then
    assertAll(
        () -> assertTrue(room.isPresent()),
        () -> assertEquals(room.get().getRoomId().getUuid(), member1Room1.getRoomId().getUuid())
    );
  }
}
