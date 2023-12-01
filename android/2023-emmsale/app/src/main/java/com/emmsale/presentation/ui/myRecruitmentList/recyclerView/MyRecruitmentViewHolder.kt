package com.emmsale.presentation.ui.myRecruitmentList.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemMyPostBinding
import com.emmsale.presentation.ui.myRecruitmentList.uiState.MyRecruitmentUiState

class MyRecruitmentViewHolder(
    private val binding: ItemMyPostBinding,
    navigateToDetail: (eventId: Long, recruitmentId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            navigateToDetail(
                binding.recruitment!!.eventId,
                binding.recruitment!!.postId,
            )
        }
    }

    fun bind(myRecruitment: MyRecruitmentUiState) {
        binding.recruitment = myRecruitment
    }

    companion object {
        fun create(
            parent: ViewGroup,
            navigateToDetail: (eventId: Long, recruitmentId: Long) -> Unit,
        ): MyRecruitmentViewHolder {
            val binding =
                ItemMyPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MyRecruitmentViewHolder(binding, navigateToDetail)
        }
    }
}
