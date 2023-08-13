package com.emmsale.presentation.ui.notificationBox.recruitmentNotification.recyclerview.body

interface RecruitmentNotificationBodyClickListener {
    fun onAcceptButtonClick(notificationId: Long)
    fun onRejectButtonClick(notificationId: Long)
    fun onMoreButtonClick(memberUid: Long)
}
