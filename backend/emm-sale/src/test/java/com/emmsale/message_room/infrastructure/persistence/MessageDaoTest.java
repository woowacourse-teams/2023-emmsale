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
    final Member member = memberRepository.save(unsavedMember);

    roomRepository.saveAll(List.of(
        new Room(new RoomId("feed014c-33f7-418c-8841-5553db5f22c1", member.getId()),
            LocalDateTime.parse("2023-09-05T16:26:20.751352")),
        new Room(new RoomId("feed014c-33f7-418c-8841-5553db5f22c1", 2L),
            LocalDateTime.parse("2023-09-07T16:48:24")),
        new Room(new RoomId("feed014c-33f7-418c-8841-5553db5f22c2", member.getId()),
            LocalDateTime.parse("2023-09-07T16:48:24")),
        new Room(new RoomId("feed014c-33f7-418c-8841-5553db5f22c2", 3L),
            LocalDateTime.parse("2023-09-07T16:48:24")),
        new Room(new RoomId("feed014c-33f7-418c-8841-5553db5f22c3", member.getId()),
            LocalDateTime.parse("2023-10-07T16:48:24")),
        new Room(new RoomId("feed014c-33f7-418c-8841-5553db5f22c3", 4L),
            LocalDateTime.parse("2023-08-07T16:45:39"))
    ));

    final Message resultMessage1 = new Message(
        "방1메시지3",
        member.getId(),
        "feed014c-33f7-418c-8841-5553db5f22c1",
        LocalDateTime.parse("2023-10-07T16:45:39")
    );
    final Message resultMessage2 = new Message(
        "방2메시지4",
        3L,
        "feed014c-33f7-418c-8841-5553db5f22c2",
        LocalDateTime.parse("2023-10-07T16:45:39")
    );
    messageRepository.saveAll(
        List.of(
            new Message("방1메시지1", member.getId(), "feed014c-33f7-418c-8841-5553db5f22c1",
                LocalDateTime.parse("2023-05-07T16:45:39")),
            new Message("방1메시지2", member.getId(), "feed014c-33f7-418c-8841-5553db5f22c1",
                LocalDateTime.parse("2023-06-07T16:45:39")),
            resultMessage1,
            resultMessage2,
            new Message("방3메시지5", member.getId(), "feed014c-33f7-418c-8841-5553db5f22c3",
                LocalDateTime.parse("2023-08-07T16:45:39"))
        )
    );

    memberRepository.save(new Member(3L, "image", "username3"))
        .updateName("member3");
    memberRepository.save(new Member(4L, "image", "username3"))
        .updateName("member4");
    memberRepository.save(new Member(5L, "image", "username3"))
        .updateName("member5");

    final Member recentlyMessage1Owner =
        memberRepository.findById(resultMessage1.getSenderId()).get();
    final Member recentlyMessage2Owner =
        memberRepository.findById(resultMessage2.getSenderId()).get();

    final List<MessageOverview> actual = List.of(
        new MessageOverview(
            resultMessage1.getId(), resultMessage1.getContent(),
            resultMessage1.getCreatedAt(), resultMessage1.getSenderId(),
            resultMessage1.getRoomId(), recentlyMessage1Owner.getName()),
        new MessageOverview(
            resultMessage2.getId(), resultMessage2.getContent(),
            resultMessage2.getCreatedAt(), resultMessage2.getSenderId(),
            resultMessage2.getRoomId(), recentlyMessage2Owner.getName())
    );

    //when
    final List<MessageOverview> expect = messageDao.findRecentlyMessages(member.getId());

    //then
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);
  }
}
