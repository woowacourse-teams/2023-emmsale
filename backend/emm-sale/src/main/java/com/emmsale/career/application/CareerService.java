package com.emmsale.career.application;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.emmsale.career.application.dto.ActivityResponse;
import com.emmsale.career.application.dto.CareerResponse;
import com.emmsale.career.domain.ActivityType;
import com.emmsale.career.domain.Career;
import com.emmsale.career.domain.CareerRepository;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CareerService {

  private final CareerRepository careerRepository;

  public List<CareerResponse> findAll() {
    final EnumMap<ActivityType, List<Career>> groupByActivityType = careerRepository.findAll()
        .stream()
        .sorted(comparing(career -> career.getName().toLowerCase()))
        .collect(
            groupingBy(Career::getActivityType, () -> new EnumMap<>(ActivityType.class), toList())
        );

    final List<CareerResponse> responses = new ArrayList<>();

    for (final Entry<ActivityType, List<Career>> entry : groupByActivityType.entrySet()) {
      final List<ActivityResponse> activityResponse = entry.getValue()
          .stream()
          .map(it -> new ActivityResponse(it.getId(), it.getName()))
          .collect(toList());

      responses.add(new CareerResponse(entry.getKey().getValue(), activityResponse));
    }

    return responses;
  }
}
