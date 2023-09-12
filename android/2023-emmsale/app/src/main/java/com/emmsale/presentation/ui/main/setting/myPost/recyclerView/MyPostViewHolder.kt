package com.emmsale.presentation.ui.main.setting.myPost.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemMyPostBinding
import com.emmsale.presentation.ui.main.setting.myPost.uiState.MyPostUiState

class MyPostViewHolder(
    private val binding: ItemMyPostBinding,
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
                ItemMyPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MyPostViewHolder(binding, navigateToDetail)
        }
    }
}