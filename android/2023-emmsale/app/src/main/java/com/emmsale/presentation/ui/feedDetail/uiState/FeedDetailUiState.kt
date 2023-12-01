package com.emmsale.presentation.ui.feedDetail.uiState

import com.emmsale.data.model.Feed
import com.emmsale.data.model.Member
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.FetchResultUiState
import java.time.LocalDateTime

data class FeedDetailUiState(
    override val fetchResult: FetchResult,
    val feedDetail: Feed,
    val comments: List<CommentUiState>,
) : FetchResultUiState() {

    val isUpdated: Boolean = feedDetail.createdAt != feedDetail.updatedAt

    val commentsCount: Int = comments.count { !it.comment.isDeleted }

    fun highlightComment(commentId: Long) = copy(
        comments = comments.map { if (it.comment.id == commentId) it.highlight() else it },
    )

    fun unhighlightComment(commentId: Long) = copy(
        comments = comments.map { if (it.comment.id == commentId) it.unhighlight() else it },
    )

    companion object {
        val Loading: FeedDetailUiState = FeedDetailUiState(
            fetchResult = FetchResult.LOADING,
            feedDetail = Feed(
                id = 0,
                eventId = 0,
                title = "",
                content = "",
                writer = Member(),
                imageUrls = emptyList(),
                commentCount = 0,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
            ),
            comments = emptyList(),
        )
    }
}
