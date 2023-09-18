package com.emmsale.comment.infrastructure.persistence;

import com.emmsale.comment.infrastructure.persistence.dto.FeedCommentCount;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentDao {

  private static final RowMapper<FeedCommentCount> FEED_COMMENT_COUNT_ROW_MAPPER = (rs, rowNum) ->
      new FeedCommentCount(rs.getLong("feed_id"), rs.getLong("comment_count"));

  private final NamedParameterJdbcTemplate namedJdbcTemplate;

  public List<FeedCommentCount> findCommentCountByFeedIds(final List<Long> feedIds) {
    final MapSqlParameterSource parameters = new MapSqlParameterSource("ids", feedIds);
    final String sql = "SELECT c.feed_id, COUNT(c.id) AS comment_count " +
        "FROM comment c " +
        "WHERE c.feed_id in (:ids) " +
        "AND c.is_deleted = false " +
        "GROUP BY c.feed_id";

    return namedJdbcTemplate.query(sql, parameters, FEED_COMMENT_COUNT_ROW_MAPPER);
  }
}
