package com.emmsale.presentation.ui.main.myProfile.editMyProfile.uiState

import com.emmsale.presentation.common.livedata.error.ErrorEvent

enum class EditMyProfileErrorEvent : ErrorEvent {
    MEMBER_FETCHING, DESCRIPTION_UPDATE, ACTIVITIES_FETCHING
}
