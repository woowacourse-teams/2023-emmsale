package com.emmsale.presentation.common

sealed interface NetworkUiEvent {
    data class Unexpected(val errorMessage: String) : NetworkUiEvent
    object FetchFail : NetworkUiEvent
    object RequestFailByNetworkError : NetworkUiEvent
}
