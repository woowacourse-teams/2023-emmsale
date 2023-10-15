package com.emmsale.presentation.ui.editMyProfile.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.data.model.Activity

class FieldsAdapter(
    private val removeField: (activityId: Long) -> Unit,
) : ListAdapter<Activity, FieldViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldViewHolder {
        return FieldViewHolder.create(parent, removeField)
    }

    override fun onBindViewHolder(holder: FieldViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<Activity>() {
            override fun areItemsTheSame(
                oldItem: Activity,
                newItem: Activity,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Activity,
                newItem: Activity,
            ): Boolean = oldItem == newItem
        }
    }
}
