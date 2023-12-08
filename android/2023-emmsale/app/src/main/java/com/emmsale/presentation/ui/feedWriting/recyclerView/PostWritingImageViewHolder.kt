package com.emmsale.presentation.ui.feedWriting.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemWritingImageBinding

class PostWritingImageViewHolder(
    private val binding: ItemWritingImageBinding,
    private val deleteImage: (String) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(imageUrl: String) {
        binding.imageUrl = imageUrl
        binding.onImageDeleteButtonClick = deleteImage
    }

    companion object {
        fun create(
            parent: ViewGroup,
            deleteImage: (String) -> Unit,
        ): PostWritingImageViewHolder {
            val binding = ItemWritingImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
            return PostWritingImageViewHolder(binding, deleteImage)
        }
    }
}
