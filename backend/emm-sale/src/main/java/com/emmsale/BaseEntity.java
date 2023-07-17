package com.emmsale;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class BaseEntity {

  @Column(updatable = false, nullable = false)
  @CreatedDate
  private LocalDateTime createdAt;
  @Column(nullable = false)
  @LastModifiedDate
  private LocalDateTime updatedAt;
}
