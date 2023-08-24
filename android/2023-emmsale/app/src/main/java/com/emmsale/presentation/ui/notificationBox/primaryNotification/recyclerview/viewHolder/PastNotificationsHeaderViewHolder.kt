package com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemPrimarynotificationPastNotificationsHeaderBinding

class PastNotificationsHeaderViewHolder(
    binding: ItemPrimarynotificationPastNotificationsHeaderBinding,
    private val deleteAllPastNotifications: () -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.tvPrimarynotificationAllDeleteButton.setOnClickListener {
            deleteAllPastNotifications()
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            deleteAllPastNotifications: () -> Unit,
        ): PastNotificationsHeaderViewHolder {
            val binding = ItemPrimarynotificationPastNotificationsHeaderBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

            return PastNotificationsHeaderViewHolder(
                binding = binding,
                deleteAllPastNotifications = deleteAllPastNotifications,
            )
        }
    }
}
