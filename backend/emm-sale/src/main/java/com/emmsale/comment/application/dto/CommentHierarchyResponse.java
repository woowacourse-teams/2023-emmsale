package com.emmsale.comment.application.dto;

import com.emmsale.base.BaseEntity;
import com.emmsale.comment.domain.Comment;
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

  public static List<CommentHierarchyResponse> from(final List<Comment> comments) {

    final Map<Comment, List<Comment>> groupedByParent =
        groupingByParentAndSortedByCreatedAt(comments);

    final List<CommentHierarchyResponse> result = new ArrayList<>();

    for (final Entry<Comment, List<Comment>> entry : groupedByParent.entrySet()) {
      final Comment parentComment = entry.getKey();
      final List<CommentResponse> childCommentResponses =
          mapToCommentResponse(entry, parentComment);

      result.add(
          new CommentHierarchyResponse(CommentResponse.from(parentComment), childCommentResponses)
      );
    }

    return result;
  }

  private static LinkedHashMap<Comment, List<Comment>> groupingByParentAndSortedByCreatedAt(
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
      final Comment parentComment
  ) {
    return entry.getValue().stream()
        .filter(it -> isNotSameKeyAndValue(parentComment, it))
        .map(CommentResponse::from)
        .sorted(Comparator.comparing(CommentResponse::getCreatedAt))
        .collect(Collectors.toList());
  }

  private static boolean isNotSameKeyAndValue(final Comment parentComment, final Comment target) {
    return !target.equals(parentComment);
  }
}
