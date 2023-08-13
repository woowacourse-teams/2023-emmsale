package com.emmsale.presentation.ui.notificationBox.recruitmentNotification.recyclerview.header

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R
import com.emmsale.databinding.ItemRecruitmentNotificationHeaderBinding
import com.emmsale.presentation.ui.notificationBox.recruitmentNotification.recyclerview.body.RecruitmentNotificationBodyAdapter
import com.emmsale.presentation.ui.notificationBox.recruitmentNotification.recyclerview.body.RecruitmentNotificationBodyClickListener
import com.emmsale.presentation.ui.notificationBox.recruitmentNotification.uistate.RecruitmentNotificationHeaderUiState

class RecruitmentNotificationHeaderViewHolder(
    parent: ViewGroup,
    recruitmentNotificationHeaderClickListener: RecruitmentNotificationHeaderClickListener,
    recruitmentNotificationBodyClickListener: RecruitmentNotificationBodyClickListener,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.item_recruitment_notification_header, parent, false),
) {
    private val binding = ItemRecruitmentNotificationHeaderBinding.bind(itemView)
    private val recruitmentNotificationBodyAdapter =
        RecruitmentNotificationBodyAdapter(recruitmentNotificationBodyClickListener)

    init {
        binding.notificationHeaderClickListener = recruitmentNotificationHeaderClickListener
    }

    fun bind(header: RecruitmentNotificationHeaderUiState) {
        binding.header = header
        binding.rvNotificationBody.adapter = recruitmentNotificationBodyAdapter
        recruitmentNotificationBodyAdapter.submitList(header.notifications)
    }
}
