package com.emmsale.presentation.ui.recruitmentNotificationList.recyclerView.body

interface RecruitmentNotificationBodyClickListener {
    fun onAcceptButtonClick(notificationId: Long)
    fun onRejectButtonClick(notificationId: Long)
    fun onMoreButtonClick(notificationId: Long)
    fun onOpenChatButtonClick(openChatUrl: String)
    fun onSenderImageClick(memberId: Long)
}
