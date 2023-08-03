package com.emmsale.presentation.ui.notificationBox.recyclerview.body

interface NotificationBodyClickListener {
    fun onClickBody(notificationId: Long, otherUid: Long)
    fun onAccept(notificationId: Long)
    fun onReject(notificationId: Long)
}
