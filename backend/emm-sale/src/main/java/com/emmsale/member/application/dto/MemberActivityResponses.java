package com.emmsale.member.application.dto;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.emmsale.activity.domain.Activity;
import com.emmsale.activity.domain.ActivityType;
import com.emmsale.member.domain.MemberActivity;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberActivityResponses {

  private final String activityType;
  private final List<MemberActivityResponse> memberActivityResponses;

  public static List<MemberActivityResponses> from(final List<MemberActivity> memberActivities) {
    final EnumMap<ActivityType, List<Activity>> groupByActivityType =
        groupingByActivityTypeAndSortedByActivityName(memberActivities);

    final List<MemberActivityResponses> responses = new ArrayList<>();

    for (final Entry<ActivityType, List<Activity>> entry : groupByActivityType.entrySet()) {
      final List<MemberActivityResponse> activityResponse =
          mapToMemberActivityResponses(entry);

      responses.add(new MemberActivityResponses(entry.getKey().getValue(), activityResponse));
    }

    return responses;
  }

  private static List<MemberActivityResponse> mapToMemberActivityResponses(
      final Entry<ActivityType, List<Activity>> entry
  ) {
    return entry.getValue()
        .stream()
        .map(it -> new MemberActivityResponse(it.getId(), it.getName()))
        .collect(toList());
  }

  private static EnumMap<ActivityType, List<Activity>> groupingByActivityTypeAndSortedByActivityName(
      final List<MemberActivity> memberActivities
  ) {
    return memberActivities
        .stream()
        .map(MemberActivity::getActivity)
        .sorted(comparing(activity -> activity.getName().toLowerCase()))
        .collect(
            groupingBy(Activity::getActivityType, () -> new EnumMap<>(ActivityType.class), toList())
        );
  }
}
