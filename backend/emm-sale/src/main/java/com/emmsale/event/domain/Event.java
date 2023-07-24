package com.emmsale.event.domain;

import com.emmsale.base.BaseEntity;
import com.emmsale.comment.domain.Comment;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Event extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private String name;
  @Column(nullable = false)
  private String location;
  @Column(nullable = false)
  private LocalDateTime startDate;
  @Column(nullable = false)
  private LocalDateTime endDate;
  @Column(nullable = false)
  private String informationUrl;
  @OneToMany(mappedBy = "event")
  private List<EventTag> tags;
  @OneToMany(mappedBy = "event")
  private List<Comment> comments;

  //@OneToMany
  //private List<Member> participants;
}
