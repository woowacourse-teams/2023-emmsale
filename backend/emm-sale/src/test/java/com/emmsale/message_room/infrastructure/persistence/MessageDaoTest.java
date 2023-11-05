package com.emmsale.message_room.infrastructure.persistence;

import com.emmsale.helper.JpaRepositorySliceTestHelper;
import com.emmsale.member.domain.Member;
import com.emmsale.member.domain.MemberRepository;
import com.emmsale.message_room.domain.Message;
import com.emmsale.message_room.domain.MessageRepository;
import com.emmsale.message_room.domain.Room;
import com.emmsale.message_room.domain.RoomId;
import com.emmsale.message_room.domain.RoomRepository;
import com.emmsale.message_room.infrastructure.persistence.dto.MessageOverview;
import java.time.LocalDateTime;
import java.util.List;
import javax.sql.DataSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class MessageDaoTest extends JpaRepositorySliceTestHelper {

  @Autowired
  private RoomRepository roomRepository;
  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private DataSource dataSource;
  private MessageDao messageDao;

  @BeforeEach
  void init() {
    messageDao = new MessageDao(new JdbcTemplate(dataSource));
  }

  @Test
  @DisplayName("findRecentlyMessages() : 사용자가 참여하고 있는 Room 중에서 가장 최근에 받은 메시지들을 조회할 수 있다.")
  void test_findRecentlyMessages() throws Exception {
    //given
    final Member unsavedMember = new Member(
        3333L,
        "imageUrl",
        "asdfdfas"
    );
    unsavedMember.updateName("member1");
    final Member loginMember = memberRepository.save(unsavedMember);

    final Member member1 = new Member(3L, "image", "username3");
    member1.updateName("member3");
    final Member room1Interlocutor = memberRepository.save(member1);

    final Member member2 = new Member(4L, "image", "username3");
    member2.updateName("member4");
    final Member room2Interlocutor = memberRepository.save(member2);

    final Member member3 = new Member(5L, "image", "username3");
    member3.updateName("member5");
    final Member room3Interlocutor = memberRepository.save(member3);

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

    final List<MessageOverview> actual = List.of(
        new MessageOverview(
            resultMessage1.getId(),
            resultMessage1.getContent(),
            resultMessage1.getSender().getId(),
            resultMessage1.getCreatedAt(),
            resultMessage1.getRoomId()
        ),
        new MessageOverview(
            resultMessage2.getId(),
            resultMessage2.getContent(),
            resultMessage2.getSender().getId(),
            resultMessage2.getCreatedAt(),
            resultMessage2.getRoomId()
        )
    );

    //when
    final List<MessageOverview> expect = messageDao.findRecentlyMessages(loginMember.getId());

    //then
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);
  }
}
