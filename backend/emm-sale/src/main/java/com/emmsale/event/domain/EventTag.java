package com.emmsale.event.domain;

import com.emmsale.tag.domain.Tag;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class EventTag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id", nullable = false)
  private Event event;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tag_id", nullable = false)
  private Tag tag;
}
