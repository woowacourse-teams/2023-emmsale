package com.emmsale.presentation.ui.notificationBox.recruitmentNotification.recyclerview.body

interface RecruitmentNotificationBodyClickListener {
    fun onClickNotificationBody(notificationId: Long, otherUid: Long)
    fun onRecruitmentAccept(notificationId: Long)
    fun onRecruitmentReject(notificationId: Long)
}
