package com.emmsale.image.domain.repository;

import com.emmsale.image.domain.Image;
import com.emmsale.image.domain.ImageType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
  
  List<Image> findAllByTypeAndContentId(final ImageType type, final Long contentId);
}
