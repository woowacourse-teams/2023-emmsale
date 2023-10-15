package com.emmsale.presentation.ui.profile.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.data.model.Activity

class ActivitiesAdapter : ListAdapter<Activity, ActivityViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        return ActivityViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<Activity>() {
            override fun areItemsTheSame(
                oldItem: Activity,
                newItem: Activity,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Activity,
                newItem: Activity,
            ): Boolean = oldItem == newItem
        }
    }
}
