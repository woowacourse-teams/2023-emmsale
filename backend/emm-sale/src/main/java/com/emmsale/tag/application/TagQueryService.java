package com.emmsale.tag.application;

import static java.util.stream.Collectors.toUnmodifiableList;

import com.emmsale.tag.application.dto.TagResponse;
import com.emmsale.tag.domain.TagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TagQueryService {

  private final TagRepository tagRepository;

  public List<TagResponse> findAll() {
    return tagRepository.findAll()
        .stream()
        .map(TagResponse::from)
        .collect(toUnmodifiableList());
  }
}
