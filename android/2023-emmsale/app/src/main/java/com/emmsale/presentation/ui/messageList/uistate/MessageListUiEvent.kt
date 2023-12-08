package com.emmsale.presentation.ui.messageList.uistate

// enum class MessageListUiEvent {
//     MESSAGE_LIST_FIRST_LOADED,
//     MESSAGE_SENDING,
//     MESSAGE_SENT_FAILED,
//     MESSAGE_SENT_REFRESHED,
//     NOT_FOUND_OTHER_MEMBER,
// }

sealed interface MessageListUiEvent {
    object MessageSendComplete : MessageListUiEvent
    object MessageSendFail : MessageListUiEvent
}
