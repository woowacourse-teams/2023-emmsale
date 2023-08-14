package com.emmsale.presentation.ui.notificationBox.primaryNotification.recyclerview

enum class PrimaryNotificationViewType(val viewType: Int) {
    RECENT_HEADER(0),
    PAST_HEADER(1),
    COMMENT(2),
    INTEREST_EVENT(3),
    ;

    companion object {
        private const val INVALID_VIEW_TYPE_ERROR_MESSAGE = "올바르지 않은 ViewType 입니다."

        fun of(viewType: Int): PrimaryNotificationViewType = when (viewType) {
            RECENT_HEADER.viewType -> RECENT_HEADER
            PAST_HEADER.viewType -> PAST_HEADER
            COMMENT.viewType -> COMMENT
            INTEREST_EVENT.viewType -> INTEREST_EVENT
            else -> throw IllegalArgumentException(INVALID_VIEW_TYPE_ERROR_MESSAGE)
        }
    }
}
