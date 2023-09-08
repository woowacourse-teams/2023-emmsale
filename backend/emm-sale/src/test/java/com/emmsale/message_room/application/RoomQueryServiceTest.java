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
    final Member member = memberRepository.findById(1L).get();
    member.updateName("member1");
    memberRepository.save(member);

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

    final Member member3 = new Member(3L, "image", "username3");
    member3.updateName("member3");
    memberRepository.save(member3);
    final Member member4 = new Member(4L, "image", "username4");
    member4.updateName("member4");
    memberRepository.save(member4);
    final Member member5 = new Member(5L, "image", "username5");
    member5.updateName("member5");
    memberRepository.save(member5);

    final Member recentlyMessage1Owner =
        memberRepository.findById(resultMessage1.getSenderId()).get();
    final Member recentlyMessage2Owner =
        memberRepository.findById(resultMessage2.getSenderId()).get();

    final MessageOverview messageOverview1 = new MessageOverview(
        resultMessage1.getId(),
        resultMessage1.getContent(),
        resultMessage1.getCreatedAt(),
        recentlyMessage1Owner.getId(),
        resultMessage1.getRoomId(),
        recentlyMessage1Owner.getName()
    );

    final MessageOverview messageOverview2 = new MessageOverview(
        resultMessage2.getId(),
        resultMessage2.getContent(),
        resultMessage2.getCreatedAt(),
        resultMessage2.getId(),
        resultMessage2.getRoomId(),
        recentlyMessage2Owner.getName()
    );

    final List<RoomResponse> actual = List.of(
        RoomResponse.from(messageOverview1),
        RoomResponse.from(messageOverview2)
    );

    //when
    final List<RoomResponse> expect = roomQueryService.findAll(member, member.getId());

    //then
    assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expect);
  }
}
