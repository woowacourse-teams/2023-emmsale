package com.emmsale.presentation.ui.editMyProfile.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemEditmyprofileActivityBinding
import com.emmsale.model.Activity

class ActivityViewHolder(
    private val binding: ItemEditmyprofileActivityBinding,
    private val removeActivity: (activityId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.ivEditmyprfileactivitiesActivityRemoveButton.setOnClickListener {
            removeActivity(binding.activity?.id ?: return@setOnClickListener)
        }
    }

    fun bind(activity: Activity) {
        binding.activity = activity
    }

    companion object {
        fun create(
            parent: ViewGroup,
            removeActivity: (activityId: Long) -> Unit,
        ): ActivityViewHolder {
            val binding = ItemEditmyprofileActivityBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

            return ActivityViewHolder(binding, removeActivity)
        }
    }
}
