package com.emmsale.tag.application;

import com.emmsale.tag.application.dto.TagResponse;
import com.emmsale.tag.domain.TagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TagQueryService {

  private final TagRepository tagRepository;

  public List<TagResponse> findAll() {
    return null;
  }
}
