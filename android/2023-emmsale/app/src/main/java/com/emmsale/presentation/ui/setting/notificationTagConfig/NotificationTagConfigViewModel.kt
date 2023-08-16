package com.emmsale.presentation.ui.setting.notificationTagConfig

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.eventTag.EventTagRepository
import com.emmsale.data.token.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.setting.notificationTagConfig.uistate.NotificationTagsUiState
import kotlinx.coroutines.launch

class NotificationTagConfigViewModel(
    private val tokenRepository: TokenRepository,
    private val eventTagRepository: EventTagRepository,
) : ViewModel() {

    private val _notificationTags = NotNullMutableLiveData(NotificationTagsUiState())
    val notificationTags: NotNullLiveData<NotificationTagsUiState> = _notificationTags

    init {
        fetchNotificationTags()
    }

    private fun fetchNotificationTags() {
        _notificationTags.value = _notificationTags.value.copy(isLoading = true)

        viewModelScope.launch {
            val memberId = tokenRepository.getToken()?.uid ?: return@launch
            when (val result = eventTagRepository.getInterestEventTags(memberId)) {
                is ApiSuccess -> _notificationTags.value = NotificationTagsUiState.from(result.data)
                is ApiError, is ApiException ->
                    _notificationTags.value = NotificationTagsUiState.FETCHING_ERROR
            }
        }
    }

    companion object {
        val factory = ViewModelFactory {
            NotificationTagConfigViewModel(
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
                eventTagRepository = KerdyApplication.repositoryContainer.eventTagRepository,
            )
        }
    }
}
