package com.emmsale.presentation.ui.feedDetail.recyclerView

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.presentation.ui.feedDetail.uiState.FeedDetailUiState

class FeedDetailAdapter(
    private val onProfileImageClick: (authorId: Long) -> Unit,
) : RecyclerView.Adapter<FeedDetailViewHolder>() {
    private val items: MutableList<FeedDetailUiState> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedDetailViewHolder =
        FeedDetailViewHolder.from(parent, onProfileImageClick)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: FeedDetailViewHolder, position: Int) {
        holder.bind(items[0])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFeedDetail(feedDetailUiState: FeedDetailUiState) {
        items.clear()
        items.add(feedDetailUiState)
        notifyDataSetChanged()
    }
}
