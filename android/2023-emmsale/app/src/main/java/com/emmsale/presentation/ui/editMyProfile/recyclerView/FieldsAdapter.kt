package com.emmsale.presentation.ui.editMyProfile.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.presentation.ui.editMyProfile.uiState.ActivityUiState

class FieldsAdapter(
    private val removeField: (activityId: Long) -> Unit,
) : ListAdapter<ActivityUiState, FieldViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldViewHolder {
        return FieldViewHolder.create(parent, removeField)
    }

    override fun onBindViewHolder(holder: FieldViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<ActivityUiState>() {
            override fun areItemsTheSame(
                oldItem: ActivityUiState,
                newItem: ActivityUiState,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ActivityUiState,
                newItem: ActivityUiState,
            ): Boolean = oldItem == newItem
        }
    }
}
