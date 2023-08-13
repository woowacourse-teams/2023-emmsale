package com.emmsale.presentation.ui.notificationBox.recruitmentNotification.recyclerview.body

interface RecruitmentNotificationBodyClickListener {
    fun onRecruitmentAccept(notificationId: Long)
    fun onRecruitmentReject(notificationId: Long)
    fun onMoreButtonClicked(notificationId: Long)
}
