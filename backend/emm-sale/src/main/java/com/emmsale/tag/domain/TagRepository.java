package com.emmsale.tag.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

  List<Tag> findByNameIn(List<String> tagNames);
  boolean existsTagByName(String name);
  Optional<Tag> findByName(String name);
}
