package com.emmsale.image.domain.repository;

import com.emmsale.image.domain.Image;
import com.emmsale.image.domain.ImageType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ImageRepository extends JpaRepository<Image, Long> {

  List<Image> findAllByTypeAndContentId(final ImageType type, final Long contentId);

  @Query("select i from Image i where i.type='EVENT' and i.order=0 and i.contentId in :eventIds")
  List<Image> findAllThumbnailByEventIdIn(final List<Long> eventIds);

  @Query("select i from Image i where i.type = 'FEED' and i.contentId in :feedIds")
  List<Image> findAllByFeedIdIn(List<Long> feedIds);


  @Query("select i from Image i where i.type = 'FEED' and i.contentId = :feedId")
  List<Image> findAllByFeedId(Long feedId);
}
