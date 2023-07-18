package com.emmsale.member.application.dto;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.emmsale.career.domain.ActivityType;
import com.emmsale.career.domain.Career;
import com.emmsale.member.domain.MemberCareer;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberCareerResponse {

  private final String activityName;
  private final List<MemberActivityResponse> memberActivityResponses;

  public static List<MemberCareerResponse> from(final List<MemberCareer> memberCareers) {
    final EnumMap<ActivityType, List<Career>> groupByActivityType =
        groupingByActivityTypeAndSortedByCareerName(memberCareers);

    final List<MemberCareerResponse> responses = new ArrayList<>();

    for (final Entry<ActivityType, List<Career>> entry : groupByActivityType.entrySet()) {
      final List<MemberActivityResponse> activityResponse =
          mapToMemberActivityResponses(entry);

      responses.add(new MemberCareerResponse(entry.getKey().getValue(), activityResponse));
    }

    return responses;
  }

  private static List<MemberActivityResponse> mapToMemberActivityResponses(
      final Entry<ActivityType, List<Career>> entry
  ) {
    return entry.getValue()
        .stream()
        .map(it -> new MemberActivityResponse(it.getId(), it.getName()))
        .collect(toList());
  }

  private static EnumMap<ActivityType, List<Career>> groupingByActivityTypeAndSortedByCareerName(
      final List<MemberCareer> memberCareers
  ) {
    return memberCareers
        .stream()
        .map(MemberCareer::getCareer)
        .sorted(comparing(career -> career.getName().toLowerCase()))
        .collect(
            groupingBy(Career::getActivityType, () -> new EnumMap<>(ActivityType.class), toList())
        );
  }
}
