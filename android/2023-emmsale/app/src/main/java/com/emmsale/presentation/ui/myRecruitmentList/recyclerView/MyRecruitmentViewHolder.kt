package com.emmsale.presentation.ui.myRecruitmentList.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.data.model.Recruitment
import com.emmsale.databinding.ItemMyRecruitmentBinding

class MyRecruitmentViewHolder(
    private val binding: ItemMyRecruitmentBinding,
    navigateToDetail: (eventId: Long, recruitmentId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            navigateToDetail(
                binding.recruitment!!.event.id,
                binding.recruitment!!.id,
            )
        }
    }

    fun bind(myRecruitment: Recruitment) {
        binding.recruitment = myRecruitment
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClick: (eventId: Long, recruitmentId: Long) -> Unit,
        ): MyRecruitmentViewHolder {
            val binding =
                ItemMyRecruitmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MyRecruitmentViewHolder(binding, onItemClick)
        }
    }
}
