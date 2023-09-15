package com.emmsale.presentation.common

abstract class FetchResultUiState {
    abstract val fetchResult: FetchResult
}

enum class FetchResult {
    SUCCESS, ERROR, LOADING
}
