package com.emmsale.presentation.ui.profile.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.model.Activity
import com.emmsale.databinding.ItemProfileActivitiesBinding

class ActivityViewHolder(
    private val binding: ItemProfileActivitiesBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(activity: Activity) {
        binding.activity = activity
    }

    companion object {
        fun create(parent: ViewGroup): ActivityViewHolder {
            val binding = ItemProfileActivitiesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )

            return ActivityViewHolder(binding)
        }
    }
}
