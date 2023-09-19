package com.emmsale.image.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Image {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(nullable = false)
  private String name;
  
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ImageType type;
  
  @Column(nullable = false)
  private Long contentId;
  
  @Column(name = "order_number", nullable = false)
  private int order;
  
  private LocalDateTime createdAt;
  
  public Image(final String name, final ImageType type, final Long contentId, final int order,
      final LocalDateTime createdAt) {
    this.name = name;
    this.type = type;
    this.contentId = contentId;
    this.order = order;
    this.createdAt = createdAt;
  }
}
