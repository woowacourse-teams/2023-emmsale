package com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.ItemPrimarynotificationRecentNotificationsHeaderBinding

class RecentNotificationsHeaderViewHolder(
    binding: ItemPrimarynotificationRecentNotificationsHeaderBinding,
) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup): RecentNotificationsHeaderViewHolder {
            val binding = ItemPrimarynotificationRecentNotificationsHeaderBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

            return RecentNotificationsHeaderViewHolder(binding)
        }
    }
}
