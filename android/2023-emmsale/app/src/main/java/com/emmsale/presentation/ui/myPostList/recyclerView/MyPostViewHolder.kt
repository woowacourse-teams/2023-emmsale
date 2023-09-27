package com.emmsale.presentation.ui.myPostList.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemMyRecruitmentPostBinding
import com.emmsale.presentation.ui.myPostList.uiState.MyPostUiState

class MyPostViewHolder(
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

    fun bind(myPost: MyPostUiState) {
        binding.myPost = myPost
    }

    companion object {
        fun create(
            parent: ViewGroup,
            navigateToDetail: (eventId: Long, recruitmentId: Long) -> Unit,
        ): MyPostViewHolder {
            val binding =
                ItemMyRecruitmentPostBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
            return MyPostViewHolder(binding, navigateToDetail)
        }
    }
}
