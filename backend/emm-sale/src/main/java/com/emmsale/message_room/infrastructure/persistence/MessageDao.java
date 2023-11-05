package com.emmsale.message_room.infrastructure.persistence;


import com.emmsale.message_room.infrastructure.persistence.dto.MessageOverview;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MessageDao {

  private static final RowMapper<MessageOverview> ROW_MAPPER = (rs, rowNum) ->
      new MessageOverview(
          rs.getLong("id"),
          rs.getString("content"),
          rs.getLong("sender_id"),
          rs.getTimestamp("created_at").toLocalDateTime(),
          rs.getString("room_id")
      );

  private final JdbcTemplate jdbcTemplate;

  public List<MessageOverview> findRecentlyMessages(final Long memberId) {
    StringBuilder sqlBuilder = new StringBuilder();

    sqlBuilder
        .append("SELECT m2.id, m2.content, m2.sender_id, m2.created_at, m2.room_id ")
        .append("FROM room r ")
        .append("JOIN (SELECT room_id, MAX(created_at) as max_created_at ")
        .append("      FROM message ")
        .append("      GROUP BY room_id) m1 ON r.uuid = m1.room_id ")
        .append("JOIN message m2 ON m1.room_id = m2.room_id AND m1.max_created_at = m2.created_at ")
        .append("WHERE r.member_id = ? ")
        .append("AND m2.created_at > r.last_exited_time ")
        .append("ORDER BY m2.created_at DESC");

    return jdbcTemplate.query(sqlBuilder.toString(), ROW_MAPPER, memberId);
  }
}
