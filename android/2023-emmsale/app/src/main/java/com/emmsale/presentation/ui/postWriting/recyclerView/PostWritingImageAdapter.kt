package com.emmsale.presentation.ui.postWriting.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class PostWritingImageAdapter(
    private val deleteImage: (String) -> Unit,
) : ListAdapter<String, PostWritingImageViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostWritingImageViewHolder {
        return PostWritingImageViewHolder.create(parent, deleteImage)
    }

    override fun onBindViewHolder(holder: PostWritingImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(
                oldItem: String,
                newItem: String,
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: String,
                newItem: String,
            ): Boolean = oldItem == newItem
        }
    }
}
