package com.emmsale.event.domain.repository;

import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventType;
import com.emmsale.tag.domain.Tag;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class EventSpecification {

  public static Specification<Event> filterByCategory(final EventType category) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("type"), category);
  }

  public static Specification<Event> filterByTags(final List<String> tags) {
    return (root, query, criteriaBuilder) -> {
      query.distinct(true);
      Join<Event, Tag> joinedTags = root
          .join("tags", JoinType.INNER)
          .join("tag", JoinType.INNER);
      return criteriaBuilder.and(joinedTags.get("name").in(tags));
    };
  }

  public static Specification<Event> filterByPeriod(final LocalDateTime startDate,
      final LocalDateTime endDate) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.or(
            criteriaBuilder.between(root.get("eventPeriod").get("startDate"), startDate, endDate),
            criteriaBuilder.between(root.get("eventPeriod").get("endDate"), startDate, endDate),
            criteriaBuilder.and(
                criteriaBuilder.lessThanOrEqualTo(root.get("eventPeriod").get("startDate"),
                    endDate),
                criteriaBuilder.greaterThanOrEqualTo(root.get("eventPeriod").get("endDate"),
                    startDate))
        );
  }
}
