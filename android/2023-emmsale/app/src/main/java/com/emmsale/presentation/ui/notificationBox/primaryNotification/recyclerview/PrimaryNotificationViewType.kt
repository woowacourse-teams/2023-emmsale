package com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview

enum class PrimaryNotificationViewType(val viewType: Int) {
    HEADER(0),
    COMMENT(1),
    INTEREST_EVENT(2),
    ;

    companion object {
        private const val INVALID_VIEW_TYPE_ERROR_MESSAGE = "올바르지 않은 ViewType 입니다."

        fun of(viewType: Int): PrimaryNotificationViewType = when (viewType) {
            HEADER.viewType -> HEADER
            COMMENT.viewType -> COMMENT
            INTEREST_EVENT.viewType -> INTEREST_EVENT
            else -> throw IllegalArgumentException(INVALID_VIEW_TYPE_ERROR_MESSAGE)
        }
    }
}
