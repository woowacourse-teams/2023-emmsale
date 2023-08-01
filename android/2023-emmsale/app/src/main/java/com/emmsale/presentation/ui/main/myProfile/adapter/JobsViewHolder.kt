package com.emmsale.presentation.ui.main.myProfile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemMyprofileJobsBinding
import com.emmsale.presentation.ui.main.myProfile.uiState.ActivityUiState

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