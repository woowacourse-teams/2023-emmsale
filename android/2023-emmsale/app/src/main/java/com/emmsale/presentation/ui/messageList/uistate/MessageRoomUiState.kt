package com.emmsale.presentation.ui.messageList.uistate

import com.emmsale.data.message.Message
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.FetchResultUiState

data class MessagesUiState(
    override val fetchResult: FetchResult = FetchResult.LOADING,
    val messages: List<MessageUiState> = emptyList(),
) : FetchResultUiState() {
    fun toSuccess(
        messages: List<Message>,
        myUid: Long,
        otherMemberProfileUrl: String?,
    ): MessagesUiState = MessagesUiState(
        fetchResult = FetchResult.SUCCESS,
        messages = messages.map { it.mapToMessageUiState(myUid, otherMemberProfileUrl) },
    )

    private fun Message.mapToMessageUiState(
        myUid: Long,
        otherMemberProfileUrl: String?,
    ): MessageUiState = MessageUiState.from(
        myUid,
        this,
        otherMemberProfileUrl,
    )

    fun toLoading(): MessagesUiState = copy(fetchResult = FetchResult.LOADING)
    fun toError(): MessagesUiState = copy(fetchResult = FetchResult.ERROR)
}
