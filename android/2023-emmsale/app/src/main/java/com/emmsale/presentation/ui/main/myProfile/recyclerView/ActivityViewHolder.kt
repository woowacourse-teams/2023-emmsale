package com.emmsale.presentation.ui.main.myProfile.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemProfileActivitiesBinding
import com.emmsale.presentation.ui.main.myProfile.uiState.ActivityUiState

class ActivityViewHolder(
    private val binding: ItemProfileActivitiesBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(activity: ActivityUiState) {
        binding.activity = activity
    }

    companion object {
        fun create(parent: ViewGroup): ActivityViewHolder {
            val binding = ItemProfileActivitiesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )

            return ActivityViewHolder(binding)
        }
    }
}
