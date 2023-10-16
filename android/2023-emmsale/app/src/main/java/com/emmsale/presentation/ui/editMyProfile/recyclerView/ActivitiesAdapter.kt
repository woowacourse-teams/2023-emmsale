package com.emmsale.presentation.ui.editMyProfile.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.data.model.Activity

class ActivitiesAdapter(
    private val removeActivity: (activityId: Long) -> Unit,
) : ListAdapter<Activity, ActivityViewHolder>(ActivityDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        return ActivityViewHolder.create(parent, removeActivity)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
