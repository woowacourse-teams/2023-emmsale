package com.emmsale.presentation.ui.myCommentList.uiState

import com.emmsale.data.model.Comment
import java.time.format.DateTimeFormatter

data class MyCommentUiState(
    val id: Long,
    val feedId: Long,
    val feedTitle: String,
    val authorId: Long,
    val parentId: Long?,
    val content: String,
    val childCount: Int,
    val lastModifiedDate: String,
    val isUpdated: Boolean,
    val isDeleted: Boolean,
) {
    companion object {
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

        fun from(comment: Comment) = MyCommentUiState(
            id = comment.id,
            feedId = comment.feed.id,
            feedTitle = comment.feed.title,
            authorId = comment.writer.id,
            parentId = comment.parentCommentId,
            content = comment.content,
            childCount = comment.childComments.size,
            lastModifiedDate = comment.updatedAt.format(dateTimeFormatter),
            isUpdated = comment.createdAt != comment.updatedAt,
            isDeleted = comment.isDeleted,
        )
    }
}
