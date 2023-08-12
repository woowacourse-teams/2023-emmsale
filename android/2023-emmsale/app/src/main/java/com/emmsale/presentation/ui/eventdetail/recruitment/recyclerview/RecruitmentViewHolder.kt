package com.emmsale.presentation.ui.eventdetail.recruitment.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemRecruitmentBinding
import com.emmsale.presentation.ui.eventdetail.recruitment.uistate.RecruitmentUiState

class RecruitmentViewHolder(
    private val binding: ItemRecruitmentBinding,
    private val showMemberProfile: (Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener { showMemberProfile(binding.recruitment!!.memberId) }
    }

    fun bind(recruitment: RecruitmentUiState) {
        binding.recruitment = recruitment
    }

    companion object {
        fun create(
            parent: ViewGroup,
            showMemberProfile: (Long) -> Unit,
        ): RecruitmentViewHolder {
            val binding = ItemRecruitmentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )

            return RecruitmentViewHolder(binding, showMemberProfile)
        }
    }
}
