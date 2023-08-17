package com.emmsale.comment.domain;

import static com.emmsale.comment.exception.CommentExceptionType.NOT_CREATE_CHILD_CHILD_COMMENT;

import com.emmsale.base.BaseEntity;
import com.emmsale.comment.exception.CommentException;
import com.emmsale.event.domain.Event;
import com.emmsale.member.domain.Member;
import java.util.List;
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
  private static final String BLOCKED_MEMBER_CONTENT = "차단된 사용자의 댓글입니다.";

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
    this.isDeleted = false;
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
    if (isChildChildComment(parent)) {
      throw new CommentException(NOT_CREATE_CHILD_CHILD_COMMENT);
    }

    return new Comment(event, parent, member, content);
  }

  private static boolean isChildChildComment(final Comment parent) {
    return parent.parent != null;
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

  public boolean isNotMyComment(final Member member) {
    return this.member.isNotMe(member);
  }

  public boolean isNotDeleted() {
    return !isDeleted || !content.equals(DELETED_COMMENT_MESSAGE);
  }

  public Optional<Comment> getParent() {
    return Optional.ofNullable(parent);
  }

  public String getContentOrHideIfBlockedMember(final List<Long> blockedMemberIds) {
    if (blockedMemberIds.contains(member.getId())) {
      return BLOCKED_MEMBER_CONTENT;
    }
    return content;
  }

  public Long getParentIdOrSelfId() {
    if (parent == null) {
      return id;
    }
    return parent.id;
  }

  public boolean isNotOwner(final Long memberId) {
    return member.isNotMe(memberId);
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
