package com.emmsale.presentation.ui.notificationConfig

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.common.retrofit.callAdapter.NetworkError
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.common.retrofit.callAdapter.Unexpected
import com.emmsale.data.model.Config
import com.emmsale.data.repository.interfaces.ConfigRepository
import com.emmsale.data.repository.interfaces.EventTagRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.common.UiEvent
import com.emmsale.presentation.common.firebase.analytics.logChangeConfig
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.ui.notificationConfig.uiState.NotificationConfigUiEvent
import com.emmsale.presentation.ui.notificationConfig.uiState.NotificationTagsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationConfigViewModel @Inject constructor(
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
            isMessageNotificationReceive = false,
        ),
    )
    val notificationConfig: NotNullLiveData<Config> = _notificationConfig

    private val _notificationTags: NotNullMutableLiveData<NotificationTagsUiState> =
        NotNullMutableLiveData(NotificationTagsUiState.Loading)
    val notificationTags: NotNullLiveData<NotificationTagsUiState> = _notificationTags

    private val _uiEvent: NotNullMutableLiveData<UiEvent<NotificationConfigUiEvent>> =
        NotNullMutableLiveData(UiEvent(NotificationConfigUiEvent.NONE))
    val uiEvent: NotNullLiveData<UiEvent<NotificationConfigUiEvent>> = _uiEvent

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
                is Failure, NetworkError -> _notificationTags.value = NotificationTagsUiState.Error
                is Success -> _notificationTags.value = NotificationTagsUiState.Success(result.data)
                is Unexpected -> throw Throwable(result.error)
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

    fun setMessageNotificationReceiveConfig(isReceive: Boolean) {
        viewModelScope.launch {
            configRepository.saveMessageNotificationReceiveConfig(isReceive)
            fetchNotificationConfig()
        }
    }

    fun removeInterestTagById(eventTagId: Long) {
        viewModelScope.launch {
            val notificationTags = _notificationTags.value
            if (notificationTags !is NotificationTagsUiState.Success) return@launch
            val removedInterestEventTag =
                notificationTags.tags.filter { tag -> tag.id != eventTagId }

            when (
                val result =
                    eventTagRepository.updateInterestEventTags(removedInterestEventTag)
            ) {
                is Failure, NetworkError ->
                    _uiEvent.value = UiEvent(NotificationConfigUiEvent.INTEREST_TAG_REMOVE_ERROR)

                is Success -> fetchNotificationTags()
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }
}
