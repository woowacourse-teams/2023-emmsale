package com.emmsale.presentation.ui.feedDetail.recyclerView

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.data.model.Feed
import com.emmsale.presentation.ui.feedDetail.recyclerView.viewHolder.FeedDetailViewHolder

class FeedDetailAdapter(
    private val onProfileImageClick: (authorId: Long) -> Unit,
) : RecyclerView.Adapter<FeedDetailViewHolder>() {
    private val items: MutableList<Feed> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedDetailViewHolder =
        FeedDetailViewHolder.from(parent, onProfileImageClick)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: FeedDetailViewHolder, position: Int) {
        holder.bind(items[0])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFeedDetail(feed: Feed) {
        items.clear()
        items.add(feed)
        notifyDataSetChanged()
    }
}
