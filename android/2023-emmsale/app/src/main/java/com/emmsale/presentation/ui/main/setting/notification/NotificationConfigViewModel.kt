package com.emmsale.presentation.ui.main.setting.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.model.Config
import com.emmsale.data.repository.ConfigRepository
import com.emmsale.data.repository.EventTagRepository
import com.emmsale.data.repository.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.Event
import com.emmsale.presentation.common.firebase.analytics.logChangeConfig
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.main.setting.notification.uistate.NotificationConfigUiEvent
import com.emmsale.presentation.ui.main.setting.notification.uistate.NotificationTagsUiState
import kotlinx.coroutines.launch

class NotificationConfigViewModel(
    private val tokenRepository: TokenRepository,
    private val eventTagRepository: EventTagRepository,
    private val configRepository: ConfigRepository,
) : ViewModel(), Refreshable {
    private val _notificationConfig: NotNullMutableLiveData<Config> = NotNullMutableLiveData(
        Config(
            isNotificationReceive = false,
            isFollowNotificationReceive = false,
            isCommentNotificationReceive = false,
            isInterestEventNotificationReceive = false,
            isAutoLogin = false,
        ),
    )
    val notificationConfig: NotNullLiveData<Config> = _notificationConfig

    private val _notificationTags: NotNullMutableLiveData<NotificationTagsUiState> =
        NotNullMutableLiveData(NotificationTagsUiState.Loading)
    val notificationTags: NotNullLiveData<NotificationTagsUiState> = _notificationTags

    private val _uiEvent: NotNullMutableLiveData<Event<NotificationConfigUiEvent>> =
        NotNullMutableLiveData(Event(NotificationConfigUiEvent.NONE))
    val uiEvent: NotNullLiveData<Event<NotificationConfigUiEvent>> = _uiEvent

    init {
        fetchNotificationTags()
        fetchNotificationConfig()
    }

    override fun refresh() {
        fetchNotificationConfig()
        fetchNotificationTags()
    }

    fun fetchNotificationTags() {
        viewModelScope.launch {
            val memberId = tokenRepository.getToken()?.uid ?: return@launch

            when (val result = eventTagRepository.getInterestEventTags(memberId)) {
                is ApiSuccess ->
                    _notificationTags.value = NotificationTagsUiState.Success(result.data)

                is ApiError, is ApiException ->
                    _notificationTags.value = NotificationTagsUiState.Error
            }
        }
    }

    private fun fetchNotificationConfig() {
        viewModelScope.launch {
            _notificationConfig.value = configRepository.getConfig()
        }
    }

    fun setAllNotificationReceiveConfig(isReceive: Boolean) {
        viewModelScope.launch {
            configRepository.saveAllNotificationReceiveConfig(isReceive)
            fetchNotificationConfig()
            logChangeConfig("notification_receive", isReceive)
        }
    }

    fun setFollowNotificationReceiveConfig(isReceive: Boolean) {
        viewModelScope.launch {
            configRepository.saveFollowNotificationReceiveConfig(isReceive)
            fetchNotificationConfig()
        }
    }

    fun setCommentNotificationReceiveConfig(isReceive: Boolean) {
        viewModelScope.launch {
            configRepository.saveCommentNotificationReceiveConfig(isReceive)
            fetchNotificationConfig()
        }
    }

    fun setInterestEventNotificationReceiveConfig(isReceive: Boolean) {
        viewModelScope.launch {
            configRepository.saveInterestEventNotificationReceiveConfig(isReceive)
            fetchNotificationConfig()
        }
    }

    fun removeInterestTagById(eventTagId: Long) {
        viewModelScope.launch {
            val notificationTags = _notificationTags.value
            if (notificationTags !is NotificationTagsUiState.Success) return@launch
            val removedInterestEventTag =
                notificationTags.tags.filter { tag -> tag.id != eventTagId }

            when (eventTagRepository.updateInterestEventTags(removedInterestEventTag)) {
                is ApiSuccess -> fetchNotificationTags()
                is ApiError, is ApiException ->
                    _uiEvent.value = Event(NotificationConfigUiEvent.INTEREST_TAG_REMOVE_ERROR)
            }
        }
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
