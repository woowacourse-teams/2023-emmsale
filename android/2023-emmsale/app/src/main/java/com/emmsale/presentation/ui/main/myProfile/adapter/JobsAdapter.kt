package com.emmsale.presentation.ui.main.myProfile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemMyprofileJobsBinding
import com.emmsale.presentation.ui.main.myProfile.uiState.ActivityUiState

class JobsAdapter : ListAdapter<ActivityUiState, JobsViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobsViewHolder {
        return JobsViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: JobsViewHolder, position: Int) {
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

class JobsViewHolder(
    private val binding: ItemMyprofileJobsBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(job: ActivityUiState) {
        binding.job = job
    }

    companion object {
        fun create(parent: ViewGroup): JobsViewHolder {
            val binding = ItemMyprofileJobsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            return JobsViewHolder(binding)
        }
    }
}

@BindingAdapter("myprofile_jobs")
fun setJobs(recyclerView: RecyclerView, jobs: List<ActivityUiState>) {
    (recyclerView.adapter as JobsAdapter).submitList(jobs)
}
