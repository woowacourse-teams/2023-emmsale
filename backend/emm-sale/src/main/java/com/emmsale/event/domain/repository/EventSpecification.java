package com.emmsale.event.domain.repository;

import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.EventType;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.tag.domain.Tag;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class EventSpecification {

  public static Specification<Event> filterByCategory(final EventType category) {
    if (category == null) {
      return null;
    }

    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("type"), category);
  }

  public static Specification<Event> filterByTags(final List<String> tags) {
    return (root, query, criteriaBuilder) -> {
      if (tags == null || tags.isEmpty()) {
        return null;
      }

      query.distinct(true);
      Join<Event, Tag> joinedTags = root
          .join("tags", JoinType.INNER)
          .join("tag", JoinType.INNER);
      return criteriaBuilder.and(joinedTags.get("name").in(tags));
    };
  }

  public static Specification<Event> filterByPeriod(
      final LocalDate startDate,
      final LocalDate endDate
  ) {
    if (startDate == null && endDate == null) {
      return null;
    }

    final LocalDateTime start = convertMinTimeIfNull(startDate);
    final LocalDateTime end = convertMaxTimeIfNull(endDate);

    if (end.isBefore(start)) {
      throw new EventException(EventExceptionType.START_DATE_AFTER_END_DATE);
    }

    return (root, query, criteriaBuilder) ->
        criteriaBuilder.or(
            criteriaBuilder.between(root.get("eventPeriod").get("startDate"), start, end),
            criteriaBuilder.between(root.get("eventPeriod").get("endDate"), start, end),
            criteriaBuilder.and(
                criteriaBuilder.lessThanOrEqualTo(root.get("eventPeriod").get("startDate"),
                    end),
                criteriaBuilder.greaterThanOrEqualTo(root.get("eventPeriod").get("endDate"),
                    start))
        );
  }

  private static LocalDateTime convertMinTimeIfNull(LocalDate date) {
    if (date == null) {
      return LocalDate.parse("2000-01-01").atStartOfDay();
    }
    return date.atStartOfDay();
  }

  private static LocalDateTime convertMaxTimeIfNull(LocalDate date) {
    if (date == null) {
      return LocalDate.parse("2999-12-31").atTime(23, 59, 59);
    }
    return date.atTime(23, 59, 59);
  }

  public static Specification<Event> filterByNameContainsSearchKeywords(final String keyword) {
    if (keyword == null || keyword.isBlank()) {
      return null;
    }

    return (root, query, criteriaBuilder) -> {
      final String[] keywords = keyword.trim().split(" ");

      Predicate[] predicates = new Predicate[keywords.length];

      for (int i = 0; i < keywords.length; i++) {
        predicates[i] = criteriaBuilder.like(root.get("name"), "%" + keywords[i] + "%");
      }

      return criteriaBuilder.and(predicates);
    };
  }
}
