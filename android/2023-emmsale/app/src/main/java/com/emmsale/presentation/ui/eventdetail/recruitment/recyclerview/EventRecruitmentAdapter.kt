package com.emmsale.presentation.ui.eventdetail.recruitment.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.databinding.ItemRecruitmentBinding
import com.emmsale.presentation.ui.eventdetail.recruitment.uistate.RecruitmentUiState

class EventRecruitmentAdapter(
    private val showMemberProfile: (Long) -> Unit,
) : ListAdapter<RecruitmentUiState, RecruitmentViewHolder>(diffUtil) {
    private lateinit var binding: ItemRecruitmentBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecruitmentViewHolder {
        binding = ItemRecruitmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return RecruitmentViewHolder(binding, showMemberProfile)
    }

    override fun onBindViewHolder(holder: RecruitmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<RecruitmentUiState>() {
            override fun areItemsTheSame(
                oldItem: RecruitmentUiState,
                newItem: RecruitmentUiState,
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: RecruitmentUiState,
                newItem: RecruitmentUiState,
            ): Boolean = oldItem.id == newItem.id
        }
    }
}
