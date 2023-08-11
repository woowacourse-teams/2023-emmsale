package com.emmsale.presentation.ui.notificationBox.recruitmentNotification.recyclerview.body

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.presentation.ui.notificationBox.recruitmentNotification.uistate.RecruitmentNotificationBodyUiState

class RecruitmentNotificationBodyAdapter(
    private val recruitmentNotificationBodyClickListener: RecruitmentNotificationBodyClickListener,
) : ListAdapter<RecruitmentNotificationBodyUiState, RecruitmentNotificationBodyViewHolder>(RecruitmentNotificationBodyDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecruitmentNotificationBodyViewHolder =
        RecruitmentNotificationBodyViewHolder(parent, recruitmentNotificationBodyClickListener)

    override fun onBindViewHolder(holder: RecruitmentNotificationBodyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
