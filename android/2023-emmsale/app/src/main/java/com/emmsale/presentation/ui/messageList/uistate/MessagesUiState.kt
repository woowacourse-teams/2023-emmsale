package com.emmsale.presentation.ui.messageList.uistate

import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.FetchResultUiState

data class MessagesUiState(
    override val fetchResult: FetchResult = FetchResult.LOADING,
    val messages: List<MessageUiState> = emptyList(),
) : FetchResultUiState() {

    fun toSuccess(
        messages: List<MessageUiState>,
    ): MessagesUiState = MessagesUiState(
        fetchResult = FetchResult.SUCCESS,
        messages = messages,
    )

    fun toLoading(): MessagesUiState = copy(fetchResult = FetchResult.LOADING)
    fun toError(): MessagesUiState = copy(fetchResult = FetchResult.ERROR)
}
