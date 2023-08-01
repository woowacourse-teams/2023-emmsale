package com.emmsale.presentation.ui.main.myProfile.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.presentation.ui.main.myProfile.uiState.ActivityUiState

class ActivitiesAdapter : ListAdapter<ActivityUiState, ActivityViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        return ActivityViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<ActivityUiState>() {
            override fun areItemsTheSame(
                oldItem: ActivityUiState,
                newItem: ActivityUiState,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ActivityUiState,
                newItem: ActivityUiState,
            ): Boolean = oldItem == newItem
        }
    }
}
