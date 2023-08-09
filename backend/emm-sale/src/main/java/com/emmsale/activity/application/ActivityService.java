package com.emmsale.activity.application;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.emmsale.activity.application.dto.ActivityResponse;
import com.emmsale.activity.application.dto.ActivityResponses;
import com.emmsale.activity.domain.Activity;
import com.emmsale.activity.domain.ActivityRepository;
import com.emmsale.activity.domain.ActivityType;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityService {

  private final ActivityRepository activityRepository;

  public List<ActivityResponses> findAll() {
    final EnumMap<ActivityType, List<Activity>> groupByActivityType = activityRepository.findAll()
        .stream()
        .sorted(comparing(activity -> activity.getName().toLowerCase()))
        .collect(
            groupingBy(Activity::getActivityType, () -> new EnumMap<>(ActivityType.class), toList())
        );

    final List<ActivityResponses> responses = new ArrayList<>();

    for (final Entry<ActivityType, List<Activity>> entry : groupByActivityType.entrySet()) {
      final List<ActivityResponse> activityResponse = entry.getValue()
          .stream()
          .map(it -> new ActivityResponse(it.getId(), it.getName()))
          .collect(toList());

      responses.add(new ActivityResponses(entry.getKey().getValue(), activityResponse));
    }

    return responses;
  }
}
