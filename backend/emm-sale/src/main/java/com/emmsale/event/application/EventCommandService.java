package com.emmsale.event.application;

import static com.emmsale.event.exception.EventExceptionType.NOT_FOUND_EVENT;
import static java.util.stream.Collectors.toList;

import com.emmsale.event.application.dto.EventDetailRequest;
import com.emmsale.event.application.dto.EventDetailResponse;
import com.emmsale.event.domain.Event;
import com.emmsale.event.domain.repository.EventRepository;
import com.emmsale.event.domain.repository.EventTagRepository;
import com.emmsale.event.exception.EventException;
import com.emmsale.event.exception.EventExceptionType;
import com.emmsale.event_publisher.EventPublisher;
import com.emmsale.image.application.ImageCommandService;
import com.emmsale.image.domain.AllImagesOfContent;
import com.emmsale.image.domain.ImageType;
import com.emmsale.tag.application.dto.TagRequest;
import com.emmsale.tag.domain.Tag;
import com.emmsale.tag.domain.TagRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class EventCommandService {

  private final EventRepository eventRepository;
  private final EventTagRepository eventTagRepository;
  private final TagRepository tagRepository;
  private final EventPublisher eventPublisher;
  private final ImageCommandService imageCommandService;

  public EventDetailResponse addEvent(final EventDetailRequest request,
      final List<MultipartFile> images) {
    final Event event = eventRepository.save(request.toEvent());
    final List<Tag> tags = findAllPersistTagsOrElseThrow(request.getTags());
    event.addAllEventTags(tags);

    final AllImagesOfContent savedImages = new AllImagesOfContent(imageCommandService
        .saveImages(ImageType.EVENT, event.getId(), images));

    eventPublisher.publish(event);
    final String thumbnailImageUrl = savedImages.extractThumbnailImage();
    final List<String> informationImageUrls = savedImages.extractInformationImages();
    return EventDetailResponse.from(event, thumbnailImageUrl, informationImageUrls);
  }

  public EventDetailResponse updateEvent(final Long eventId, final EventDetailRequest request,
      final List<MultipartFile> images) {
    final Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventException(NOT_FOUND_EVENT));

    final List<Tag> tags = findAllPersistTagsOrElseThrow(request.getTags());

    // TODO: 2023/09/25 더 좋은 방법을 고민해보기
    eventTagRepository.deleteAllByEventId(eventId);
    final Event updatedEvent = event.updateEventContent(
        request.getName(),
        request.getLocation(),
        request.getStartDateTime(),
        request.getEndDateTime(),
        request.getApplyStartDateTime(),
        request.getApplyEndDateTime(),
        request.getInformationUrl(),
        tags,
        request.getType(),
        request.getEventMode(),
        request.getPaymentType(),
        request.getOrganization()
    );
    imageCommandService.deleteImages(ImageType.EVENT, eventId);
    final AllImagesOfContent savedImages = new AllImagesOfContent(imageCommandService
        .saveImages(ImageType.EVENT, event.getId(), images));
    final String thumbnailImageUrl = savedImages.extractThumbnailImage();
    final List<String> informationImageUrls = savedImages.extractInformationImages();
    return EventDetailResponse.from(updatedEvent, thumbnailImageUrl, informationImageUrls);
  }

  public void deleteEvent(final Long eventId) {
    if (!eventRepository.existsById(eventId)) {
      throw new EventException(NOT_FOUND_EVENT);
    }
    imageCommandService.deleteImages(ImageType.EVENT, eventId);
    eventRepository.deleteById(eventId);
  }

  private List<Tag> findAllPersistTagsOrElseThrow(final List<TagRequest> tags) {
    if (tags == null || tags.isEmpty()) {
      return new ArrayList<>();
    }

    return tags.stream()
        .map(tag -> tagRepository.findByName(tag.getName())
            .orElseThrow(() -> new EventException(EventExceptionType.NOT_FOUND_TAG)))
        .collect(toList());
  }
}
