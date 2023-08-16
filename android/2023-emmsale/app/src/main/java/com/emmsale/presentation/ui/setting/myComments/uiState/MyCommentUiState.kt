package com.emmsale.presentation.ui.setting.myComments.uiState

import com.emmsale.data.comment.Comment
import java.time.format.DateTimeFormatter

data class MyCommentUiState(
    val id: Long,
    val eventId: Long,
    val eventName: String,
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
            eventId = comment.eventId,
            eventName = comment.eventName,
            authorId = comment.authorId,
            parentId = comment.parentId,
            content = comment.content,
            childCount = comment.childComments.size,
            lastModifiedDate = comment.updatedAt.format(dateTimeFormatter),
            isUpdated = comment.createdAt != comment.updatedAt,
            isDeleted = comment.deleted,
        )
    }
}
