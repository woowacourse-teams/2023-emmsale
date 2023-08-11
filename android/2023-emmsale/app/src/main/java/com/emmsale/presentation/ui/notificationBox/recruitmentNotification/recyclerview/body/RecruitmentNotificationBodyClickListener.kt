package com.emmsale.presentation.ui.notificationBox.recruitmentNotification.recyclerview.body

interface RecruitmentNotificationBodyClickListener {
    fun onClickBody(notificationId: Long, otherUid: Long)
    fun onAccept(notificationId: Long)
    fun onReject(notificationId: Long)
}
