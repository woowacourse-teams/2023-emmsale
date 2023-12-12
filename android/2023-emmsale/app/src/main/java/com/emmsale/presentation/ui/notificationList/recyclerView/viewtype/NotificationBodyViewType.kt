package com.emmsale.presentation.ui.notificationList.recyclerView.viewtype

enum class NotificationBodyViewType(val viewType: Int) {
    INTEREST_EVENT(0),
    CHILD_COMMENT(1),
    ;

    companion object {
        private const val INVALID_VIEW_TYPE_ERROR_MESSAGE = "올바르지 않은 ViewType 입니다."

        fun of(viewType: Int): NotificationBodyViewType = when (viewType) {
            INTEREST_EVENT.viewType -> INTEREST_EVENT
            CHILD_COMMENT.viewType -> CHILD_COMMENT
            else -> throw IllegalArgumentException(INVALID_VIEW_TYPE_ERROR_MESSAGE)
        }
    }
}
