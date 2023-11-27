package com.emmsale.image.application;

import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.feed.domain.repository.FeedRepository;
import com.emmsale.feed.exception.FeedException;
import com.emmsale.feed.exception.FeedExceptionType;
import com.emmsale.image.domain.Image;
import com.emmsale.image.domain.ImageType;
import com.emmsale.image.domain.repository.ImageRepository;
import com.emmsale.image.exception.ImageException;
import com.emmsale.image.exception.ImageExceptionType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageCommandService {
  // TODO: 2023/09/14 put 메서드 구현

  private final S3Client s3Client;
  private final ImageRepository imageRepository;
  private final EventRepository eventRepository;
  private final FeedRepository feedRepository;

  public List<Image> saveImages(final ImageType imageType, final Long contentId,
      final List<MultipartFile> multipartFiles) {
    validateContentExist(imageType, contentId);
    validateImageCount(imageType, multipartFiles);

    final List<String> imageNames = s3Client.uploadImages(multipartFiles);

    try {
      return saveImagesToDb(imageType, contentId, imageNames);
    } catch (Exception exception) {
      s3Client.deleteImages(imageNames);
      // TODO: 2023/09/15 이 동작이 실패했을 경우에 대한 처리(추후 고도화)
      throw new ImageException(ImageExceptionType.FAIL_DB_UPLOAD_IMAGE);
    }
  }

  private void validateContentExist(final ImageType imageType, final Long contentId) {
    if (imageType == ImageType.EVENT) {
      validateEventExist(contentId);
    }
    if (imageType == ImageType.FEED) {
      validateFeedExist(contentId);
    }
  }

  private void validateEventExist(final Long contentId) {
    if (!eventRepository.existsById(contentId)) {
      throw new EventException(EventExceptionType.NOT_FOUND_EVENT);
    }
  }

  private void validateFeedExist(final Long contentId) {
    if (!feedRepository.existsById(contentId)) {
      throw new FeedException(FeedExceptionType.NOT_FOUND_FEED);
    }
  }

  private void validateImageCount(final ImageType imageType,
      final List<MultipartFile> multipartFiles) {
    if (imageType.isOverMaxImageCount(multipartFiles.size())) {
      throw new ImageException(ImageExceptionType.OVER_MAX_IMAGE_COUNT);
    }
  }

  private List<Image> saveImagesToDb(final ImageType imageType, final Long contentId,
      final List<String> imageNames) {
    final List<Image> images = new ArrayList<>();
    for (int i = 0; i < imageNames.size(); i++) {
      final Image image = new Image(imageNames.get(i), imageType, contentId, i,
          LocalDateTime.now());
      images.add(imageRepository.save(image));
    }
    return images;
  }

  public void deleteImages(final ImageType imageType, final Long contentId) {
    final List<Image> images = imageRepository.findAllByTypeAndContentId(imageType, contentId);
    final List<Long> imageIds = images.stream()
        .map(Image::getId)
        .collect(Collectors.toList());
    final List<String> imageNames = images.stream()
        .map(Image::getName)
        .collect(Collectors.toList());
    imageRepository.deleteAllByIdInBatch(imageIds);
    s3Client.deleteImages(imageNames);
    // TODO: 2023/09/15 S3의 이미지가 '일부만' 삭제되는 경우에 대한 처리(추후 고도화)
  }
}
