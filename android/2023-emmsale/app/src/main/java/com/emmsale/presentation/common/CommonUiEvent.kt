package com.emmsale.presentation.common

sealed interface CommonUiEvent {
    data class Unexpected(val errorMessage: String) : CommonUiEvent
    object FetchFail : CommonUiEvent
    object RequestFailByNetworkError : CommonUiEvent
}
