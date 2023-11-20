package com.emmsale.admin.tag.application;

import static com.emmsale.admin.login.utils.AdminValidator.validateAuthorization;

import com.emmsale.member.domain.Member;
import com.emmsale.tag.application.dto.TagRequest;
import com.emmsale.tag.application.dto.TagResponse;
import com.emmsale.tag.domain.Tag;
import com.emmsale.tag.domain.TagRepository;
import com.emmsale.tag.exception.TagException;
import com.emmsale.tag.exception.TagExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TagCommandService {

  private final TagRepository tagRepository;

  public TagResponse addTag(final TagRequest request,
      final Member admin) {
    validateAuthorization(admin);
    final String name = request.getName();
    validateAlreadyExist(name);
    return TagResponse.from(tagRepository.save(new Tag(name)));
  }

  private void validateAlreadyExist(final String name) {
    if (tagRepository.existsTagByName(name)) {
      throw new TagException(TagExceptionType.ALEADY_EXIST_TAG);
    }
  }

}
