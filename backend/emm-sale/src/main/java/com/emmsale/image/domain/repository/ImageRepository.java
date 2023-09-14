package com.emmsale.image.domain.repository;

import com.emmsale.image.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
