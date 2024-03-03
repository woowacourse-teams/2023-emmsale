package com.emmsale.presentation.ui.editMyProfile.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.model.Activity
import com.emmsale.databinding.ItemEditmyprofileFieldBinding

class FieldViewHolder(
    private val binding: ItemEditmyprofileFieldBinding,
    private val removeField: (activityId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.ivEditmyprofilefieldRemove.setOnClickListener {
            removeField(binding.field?.id ?: return@setOnClickListener)
        }
    }

    fun bind(field: Activity) {
        binding.field = field
    }

    companion object {
        fun create(parent: ViewGroup, removeField: (activityId: Long) -> Unit): FieldViewHolder {
            val binding = ItemEditmyprofileFieldBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

            return FieldViewHolder(binding, removeField)
        }
    }
}
