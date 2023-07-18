package com.emmsale.presentation.base.viewmodel

sealed class LoadingUiState {
    object Success : LoadingUiState()
    object LoadingUi : LoadingUiState()
    object Error : LoadingUiState()
}
