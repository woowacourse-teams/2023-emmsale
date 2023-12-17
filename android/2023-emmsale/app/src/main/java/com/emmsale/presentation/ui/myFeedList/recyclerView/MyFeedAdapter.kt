package com.emmsale.presentation.ui.myFeedList.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.data.model.Feed
import com.emmsale.databinding.ItemMyFeedBinding

class MyFeedAdapter(
    private val navigateToDetail: (recruitmentId: Long) -> Unit,
) : ListAdapter<Feed, MyFeedViewHolder>(
    object : DiffUtil.ItemCallback<Feed>() {
        override fun areItemsTheSame(
            oldItem: Feed,
            newItem: Feed,
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: Feed,
            newItem: Feed,
        ): Boolean = oldItem.id == newItem.id
    },
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFeedViewHolder {
        val binding =
            ItemMyFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyFeedViewHolder(binding, navigateToDetail)
    }

    override fun onBindViewHolder(holder: MyFeedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
