package com.emmsale.presentation.ui.main.myProfile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemMyprofileCategoriesBinding
import com.emmsale.presentation.ui.main.myProfile.uiState.ActivityUiState

class JobsAdapter : ListAdapter<ActivityUiState, JobsViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobsViewHolder {
        return JobsViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: JobsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun replaceItems(items: List<ActivityUiState>) {
        submitList(items)
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

class JobsViewHolder(
    private val binding: ItemMyprofileCategoriesBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(job: ActivityUiState) {
        binding.job = job
    }

    companion object {
        fun create(parent: ViewGroup): JobsViewHolder {
            val binding = ItemMyprofileCategoriesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            return JobsViewHolder(binding)
        }
    }
}
