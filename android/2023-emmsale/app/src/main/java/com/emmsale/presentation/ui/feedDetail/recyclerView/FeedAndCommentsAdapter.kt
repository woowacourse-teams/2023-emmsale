package com.emmsale.presentation.ui.feedDetail.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.model.Comment
import com.emmsale.presentation.ui.feedDetail.recyclerView.viewHolder.CommentViewHolder
import com.emmsale.presentation.ui.feedDetail.recyclerView.viewHolder.FeedDetailViewHolder
import com.emmsale.presentation.ui.feedDetail.recyclerView.viewHolder.FeedOrCommentViewHolder
import com.emmsale.presentation.ui.feedDetail.uiState.CommentUiState
import com.emmsale.presentation.ui.feedDetail.uiState.FeedOrCommentUiState
import com.emmsale.presentation.ui.feedDetail.uiState.FeedUiState

class FeedAndCommentsAdapter(
    private val onAuthorImageClick: (authorId: Long) -> Unit,
    private val onCommentClick: (comment: Comment) -> Unit,
    private val onCommentMenuClick: (isWrittenByLoginUser: Boolean, comment: Comment) -> Unit,
) : ListAdapter<FeedOrCommentUiState, FeedOrCommentViewHolder>(FeedAndCommentDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedOrCommentViewHolder =
        when (viewType) {
            FeedUiState.VIEW_TYPE -> FeedDetailViewHolder(
                parent = parent,
                onAuthorImageClick = onAuthorImageClick,
            )

            CommentUiState.VIEW_TYPE -> CommentViewHolder(
                parent = parent,
                onCommentClick = onCommentClick,
                onAuthorImageClick = onAuthorImageClick,
                onCommentMenuClick = onCommentMenuClick,
            )

            else -> throw IllegalArgumentException("피드 혹은 댓글 뷰 홀더 타입이어야 합니다.")
        }

    override fun onBindViewHolder(holder: FeedOrCommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int = getItem(position).viewType
}
