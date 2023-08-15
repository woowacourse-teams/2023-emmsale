package com.emmsale.presentation.ui.main.myProfile.editMyProfile.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemEditmyprofileFieldBinding
import com.emmsale.presentation.ui.main.myProfile.editMyProfile.uiState.ActivityUiState

class FieldViewHolder(
    private val binding: ItemEditmyprofileFieldBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(field: ActivityUiState) {
        binding.field = field
    }

    companion object {
        fun create(parent: ViewGroup): FieldViewHolder {
            val binding = ItemEditmyprofileFieldBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

            return FieldViewHolder(binding)
        }
    }
}
