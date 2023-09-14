package com.emmsale.presentation.ui.recruitmentList.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemRecruitmentBinding
import com.emmsale.presentation.ui.recruitmentList.uiState.RecruitmentPostUiState

class RecruitmentViewHolder(
    private val binding: ItemRecruitmentBinding,
    private val navigateToDetail: (RecruitmentPostUiState) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener { navigateToDetail(binding.recruitment!!) }
    }

    fun bind(recruitment: RecruitmentPostUiState) {
        binding.recruitment = recruitment
    }

    companion object {
        fun create(
            parent: ViewGroup,
            navigateToDetail: (RecruitmentPostUiState) -> Unit,
        ): RecruitmentViewHolder {
            val binding = ItemRecruitmentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )

            return RecruitmentViewHolder(binding, navigateToDetail)
        }
    }
}
