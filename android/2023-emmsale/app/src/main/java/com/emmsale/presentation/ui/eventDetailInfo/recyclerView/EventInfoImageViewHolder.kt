package com.emmsale.presentation.ui.eventDetailInfo.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemEventInformationImageBinding

class EventInfoImageViewHolder(
    private val binding: ItemEventInformationImageBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(imageUrl: String) {
        binding.imageUrl = imageUrl
    }

    companion object {
        fun from(parent: ViewGroup): EventInfoImageViewHolder {
            val binding = ItemEventInformationImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
            return EventInfoImageViewHolder(binding)
        }
    }
}
