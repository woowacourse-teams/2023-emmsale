package com.emmsale.presentation.ui.myPostList.myRecruitmentPostList.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.data.model.MyRecruitmentPost
import com.emmsale.databinding.ItemMyRecruitmentPostBinding

class MyRecruitmentPostViewHolder(
    private val binding: ItemMyRecruitmentPostBinding,
    navigateToDetail: (eventId: Long, recruitmentId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            navigateToDetail(
                binding.myPost!!.eventId,
                binding.myPost!!.postId,
            )
        }
    }

    fun bind(myPost: MyRecruitmentPost) {
        binding.myPost = myPost
    }

    companion object {
        fun create(
            parent: ViewGroup,
            navigateToDetail: (eventId: Long, recruitmentId: Long) -> Unit,
        ): MyRecruitmentPostViewHolder {
            val binding =
                ItemMyRecruitmentPostBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
            return MyRecruitmentPostViewHolder(binding, navigateToDetail)
        }
    }
}
