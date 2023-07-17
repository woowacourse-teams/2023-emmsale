package com.emmsale.career.application;

import static java.util.stream.Collectors.toList;

import com.emmsale.career.application.dto.ActivityResponse;
import com.emmsale.career.application.dto.CareerResponse;
import com.emmsale.career.domain.ActivityType;
import com.emmsale.career.domain.Career;
import com.emmsale.career.domain.CareerRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CareerService {

  private final CareerRepository careerRepository;

  public List<CareerResponse> findAll() {
    final Map<ActivityType, List<Career>> groupByActivityType = careerRepository.findAll()
        .stream()
        .collect(Collectors.groupingBy(Career::getActivityType));

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
