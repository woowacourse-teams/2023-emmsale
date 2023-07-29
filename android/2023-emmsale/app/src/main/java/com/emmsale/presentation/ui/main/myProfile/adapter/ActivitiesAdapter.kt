package com.emmsale.presentation.ui.main.myProfile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemMyprofileActivitiesBinding
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
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: ActivityUiState,
                newItem: ActivityUiState,
            ): Boolean = oldItem.id == newItem.id
        }
    }
}

class ActivityViewHolder(
    private val binding: ItemMyprofileActivitiesBinding
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

@BindingAdapter("activities")
fun setActivities(recyclerView: RecyclerView, activities: List<ActivityUiState>) {
    (recyclerView.adapter as ActivitiesAdapter).submitList(activities)
}
