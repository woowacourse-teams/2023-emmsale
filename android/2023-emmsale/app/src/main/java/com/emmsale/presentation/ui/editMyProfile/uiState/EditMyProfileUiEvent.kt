package com.emmsale.presentation.ui.editMyProfile.uiState

sealed interface EditMyProfileUiEvent {
    object DescriptionUpdateFail : EditMyProfileUiEvent
    object ActivityRemoveFail : EditMyProfileUiEvent
    object ActivitiesAddFail : EditMyProfileUiEvent
    object ProfileImageUpdateFail : EditMyProfileUiEvent
}
