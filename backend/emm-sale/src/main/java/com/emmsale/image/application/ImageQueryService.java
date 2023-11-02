package com.emmsale.image.application;

import static java.util.stream.Collectors.groupingBy;

import com.emmsale.image.domain.AllImagesOfContent;
import com.emmsale.image.domain.Image;
import com.emmsale.image.domain.ImageType;
import com.emmsale.image.domain.repository.ImageRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageQueryService {

  private final ImageRepository imageRepository;

  public Map<Long, AllImagesOfContent> findImagesPerContentId(final ImageType imageType,
      final List<Long> eventIds) {
    final Map<Long, List<Image>> imagesPerEventId = imageRepository.findAllByTypeAndContentIdIn(
            imageType, eventIds)
        .stream()
        .collect(groupingBy(Image::getContentId));
    final Map<Long, AllImagesOfContent> result = new HashMap<>();
    eventIds.forEach(key ->
        result.put(key,
            new AllImagesOfContent(imagesPerEventId.getOrDefault(key, new ArrayList<>())))
    );
    return result;
  }

  public AllImagesOfContent findImagesOfContent(final ImageType imageType, final Long eventId) {
    return new AllImagesOfContent(imageRepository.findAllByTypeAndContentId(imageType, eventId));
  }

}
