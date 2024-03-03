package com.emmsale.presentation.ui.recruitmentList.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.model.Recruitment
import com.emmsale.databinding.ItemRecruitmentBinding

class RecruitmentViewHolder(
    private val binding: ItemRecruitmentBinding,
    private val navigateToDetail: (Recruitment) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener { navigateToDetail(binding.recruitment!!) }
    }

    fun bind(recruitment: Recruitment) {
        binding.recruitment = recruitment
    }

    companion object {
        fun create(
            parent: ViewGroup,
            navigateToDetail: (Recruitment) -> Unit,
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
