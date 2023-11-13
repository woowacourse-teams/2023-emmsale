package com.emmsale.activity.application;

import static java.util.stream.Collectors.toUnmodifiableList;

import com.emmsale.activity.application.dto.ActivityResponse;
import com.emmsale.activity.domain.ActivityRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityQueryService {

  private final ActivityRepository activityRepository;

  public List<ActivityResponse> findAll() {
    return activityRepository.findAll()
      .stream()
      .map(ActivityResponse::from)
      .collect(toUnmodifiableList());
  }
}
