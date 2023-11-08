package com.emmsale.presentation.ui.feedDetail.uiState

import com.emmsale.data.model.FeedDetail
import com.emmsale.data.model.Writer
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.FetchResultUiState
import java.time.LocalDateTime

data class FeedDetailUiState(
    override val fetchResult: FetchResult,
    val feedDetail: FeedDetail,
    val comments: List<CommentUiState>,
) : FetchResultUiState() {

    val isUpdated: Boolean = feedDetail.createdAt != feedDetail.updatedAt

    val commentsCount: Int = comments.count { !it.comment.deleted }

    fun highlightComment(commentId: Long) = copy(
        comments = comments.map { if (it.comment.id == commentId) it.highlight() else it },
    )

    fun unhighlightComment(commentId: Long) = copy(
        comments = comments.map { if (it.comment.id == commentId) it.unhighlight() else it },
    )

    companion object {
        val Loading: FeedDetailUiState = FeedDetailUiState(
            fetchResult = FetchResult.LOADING,
            feedDetail = FeedDetail(
                id = -1,
                eventId = -1,
                title = "",
                content = "",
                writer = Writer(
                    id = -1,
                    name = "",
                    imageUrl = "",
                ),
                imageUrls = emptyList(),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
            ),
            comments = emptyList(),
        )
    }
}
