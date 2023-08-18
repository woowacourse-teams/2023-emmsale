package com.emmsale.comment.application.dto;

import com.emmsale.base.BaseEntity;
import com.emmsale.comment.domain.Comment;
import com.emmsale.comment.exception.CommentException;
import com.emmsale.comment.exception.CommentExceptionType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CommentHierarchyResponse {

  private final CommentResponse parentComment;
  private final List<CommentResponse> childComments;

  public static CommentHierarchyResponse from(final List<Comment> comments,
      final List<Long> blockedMemberIds) {
    final List<CommentResponse> childComments = comments.stream()
        .filter(Comment::isChild)
        .map(comment -> CommentResponse.from(comment, blockedMemberIds))
        .collect(Collectors.toList());

    final CommentResponse parentComment = comments.stream()
        .filter(Comment::isRoot)
        .map(comment -> CommentResponse.from(comment, blockedMemberIds))
        .findFirst()
        .orElseThrow(() -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT));

    return new CommentHierarchyResponse(parentComment, childComments);
  }

  public static List<CommentHierarchyResponse> convertAllFrom(final List<Comment> comments,
      final List<Long> blockedMemberIds) {

    final Map<Comment, List<Comment>> groupedByParent =
        groupingByParentAndSortedByCreatedAt(comments);

    final List<CommentHierarchyResponse> result = new ArrayList<>();

    for (final Entry<Comment, List<Comment>> entry : groupedByParent.entrySet()) {
      final Comment parentComment = entry.getKey();
      final List<CommentResponse> childCommentResponses =
          mapToCommentResponse(entry, parentComment, blockedMemberIds);

      result.add(
          new CommentHierarchyResponse(
              CommentResponse.from(parentComment, blockedMemberIds),
              childCommentResponses
          )
      );
    }

    return result;
  }

  private static Map<Comment, List<Comment>> groupingByParentAndSortedByCreatedAt(
      final List<Comment> comments
  ) {
    return comments.stream()
        .sorted(Comparator.comparing(BaseEntity::getCreatedAt))
        .collect(Collectors.groupingBy(
            it -> it.getParent().orElse(it),
            LinkedHashMap::new, Collectors.toList())
        );
  }

  private static List<CommentResponse> mapToCommentResponse(
      final Entry<Comment, List<Comment>> entry,
      final Comment parentComment,
      final List<Long> blockedMemberIds
  ) {
    return entry.getValue().stream()
        .filter(it -> isNotSameKeyAndValue(parentComment, it))
        .map(comment -> CommentResponse.from(comment, blockedMemberIds))
        .sorted(Comparator.comparing(CommentResponse::getCreatedAt))
        .collect(Collectors.toList());
  }

  private static boolean isNotSameKeyAndValue(final Comment parentComment, final Comment target) {
    return !target.equals(parentComment);
  }
}
