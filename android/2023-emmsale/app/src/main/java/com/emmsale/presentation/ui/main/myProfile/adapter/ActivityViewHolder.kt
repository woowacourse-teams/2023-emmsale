package com.emmsale.presentation.ui.main.myProfile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemMyprofileActivitiesBinding
import com.emmsale.presentation.ui.main.myProfile.uiState.ActivityUiState

class ActivityViewHolder(
    private val binding: ItemMyprofileActivitiesBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(activity: ActivityUiState) {
        binding.activity = activity
    }

    companion object {
        fun create(parent: ViewGroup): ActivityViewHolder {
            val binding = ItemMyprofileActivitiesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            return ActivityViewHolder(binding)
        }
    }
}