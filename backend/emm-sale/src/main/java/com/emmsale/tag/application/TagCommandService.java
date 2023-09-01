package com.emmsale.tag.application;

import com.emmsale.tag.application.dto.TagRequest;
import com.emmsale.tag.application.dto.TagResponse;
import com.emmsale.tag.domain.Tag;
import com.emmsale.tag.domain.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TagCommandService {

  private final TagRepository tagRepository;

  public TagResponse addTag(final TagRequest request){
    return TagResponse.from(tagRepository.save(new Tag(request.getName())));
  }

}
