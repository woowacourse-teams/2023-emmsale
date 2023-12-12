package com.emmsale.presentation.ui.feedDetail.recyclerView.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.presentation.ui.feedDetail.uiState.FeedOrCommentUiState

abstract class FeedOrCommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(uiState: FeedOrCommentUiState)
}
