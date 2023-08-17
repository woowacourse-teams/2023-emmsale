package com.emmsale.presentation.ui.main.setting.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.config.ConfigRepository
import com.emmsale.data.eventTag.EventTag
import com.emmsale.data.eventTag.EventTagRepository
import com.emmsale.data.token.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.firebase.analytics.logChangeConfig
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.main.setting.notification.uistate.NotificationConfigUiState
import com.emmsale.presentation.ui.main.setting.notification.uistate.NotificationTagsUiState
import kotlinx.coroutines.launch

class NotificationConfigViewModel(
    private val tokenRepository: TokenRepository,
    private val eventTagRepository: EventTagRepository,
    private val configRepository: ConfigRepository,
) : ViewModel() {
    private val _notificationConfig = NotNullMutableLiveData(NotificationConfigUiState())
    val notificationConfig: NotNullLiveData<NotificationConfigUiState> = _notificationConfig

    private val _notificationTags = NotNullMutableLiveData(NotificationTagsUiState())
    val notificationTags: NotNullLiveData<NotificationTagsUiState> = _notificationTags

    init {
        fetchNotificationTags()
        fetchNotificationConfig()
    }

    fun fetchNotificationTags() {
        viewModelScope.launch {
            val memberId = tokenRepository.getToken()?.uid ?: return@launch

            when (val result = eventTagRepository.getInterestEventTags(memberId)) {
                is ApiSuccess -> _notificationTags.value = NotificationTagsUiState.from(result.data)
                is ApiError, is ApiException -> changeToInterestTagFetchingErrorState()
            }
        }
    }

    private fun changeToInterestTagFetchingErrorState() {
        _notificationTags.value = _notificationTags.value.copy(
            isLoading = false,
            isTagFetchingError = true,
        )
    }

    private fun fetchNotificationConfig() {
        viewModelScope.launch {
            val config = configRepository.getConfig()
            _notificationConfig.value = NotificationConfigUiState.from(config)
        }
    }

    fun setNotificationReceiveConfig(isReceive: Boolean) {
        viewModelScope.launch {
            configRepository.saveNotificationReceiveConfig(isReceive)
            updateNotificationReceiveConfig(isReceive)
            logChangeConfig("notification_receive", isReceive)
        }
    }

    private fun updateNotificationReceiveConfig(isReceive: Boolean) {
        _notificationConfig.value = notificationConfig.value.copy(
            isNotificationReceive = isReceive,
        )
    }

    fun removeInterestTagById(eventId: Long) {
        viewModelScope.launch {
            val removedInterestEventTags = notificationTags.value.conferenceTags
                .filterNot { it.id != eventId }
                .map { EventTag(id = it.id, name = it.tagName) }

            when (eventTagRepository.updateInterestEventTags(removedInterestEventTags)) {
                is ApiSuccess -> fetchNotificationTags()
                is ApiError, is ApiException -> changeToInterestTagRemoveErrorState()
            }
        }
    }

    private fun changeToInterestTagRemoveErrorState() {
        _notificationTags.value = _notificationTags.value.copy(
            isLoading = false,
            isTagRemoveError = true,
        )
    }

    companion object {
        val factory = ViewModelFactory {
            NotificationConfigViewModel(
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
                eventTagRepository = KerdyApplication.repositoryContainer.eventTagRepository,
                configRepository = KerdyApplication.repositoryContainer.configRepository,
            )
        }
    }
}
