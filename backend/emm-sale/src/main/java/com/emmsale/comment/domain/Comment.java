package com.emmsale.comment.domain;

import com.emmsale.base.BaseEntity;
import com.emmsale.event.domain.Event;
import com.emmsale.member.domain.Member;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

  private static final String DELETED_COMMENT_MESSAGE = "삭제된 댓글입니다.";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private Event event;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn
  private Comment parent;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false)
  private Member member;
  @Column(nullable = false)
  private String content;
  private boolean isDeleted;

  private Comment(
      final Event event,
      final Comment parent,
      final Member member,
      final String content
  ) {
    this.event = event;
    this.parent = parent;
    this.member = member;
    this.content = content;
  }

  public static Comment createRoot(
      final Event event,
      final Member member,
      final String content
  ) {
    return new Comment(event, null, member, content);
  }

  public static Comment createChild(
      final Event event,
      final Comment parent,
      final Member member,
      final String content
  ) {
    return new Comment(event, parent, member, content);
  }

  public void delete() {
    isDeleted = true;
    content = DELETED_COMMENT_MESSAGE;
  }

  public void modify(final String content) {
    this.content = content;
  }

  public boolean isRoot() {
    return parent == null;
  }

  public boolean isChild() {
    return parent != null;
  }

  public boolean isNotMyComment(final Long memberId) {
    return this.member.isNotMe(memberId);
  }

  public Optional<Comment> getParent() {
    return Optional.ofNullable(parent);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Comment comment = (Comment) o;
    return Objects.equals(id, comment.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
