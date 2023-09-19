package com.emmsale.presentation.ui.recruitmentNotificationList.recyclerView.body

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R
import com.emmsale.databinding.ItemRecruitmentNotificationBodyBinding
import com.emmsale.presentation.ui.recruitmentNotificationList.uiState.RecruitmentNotificationBodyUiState

class RecruitmentNotificationBodyViewHolder(
    parent: ViewGroup,
    recruitmentNotificationBodyClickListener: RecruitmentNotificationBodyClickListener,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.item_recruitment_notification_body, parent, false),
) {
    private val binding = ItemRecruitmentNotificationBodyBinding.bind(itemView)

    init {
        binding.notificationBodyClickListener = recruitmentNotificationBodyClickListener
    }

    fun bind(notification: RecruitmentNotificationBodyUiState) {
        binding.notification = notification
    }
}
