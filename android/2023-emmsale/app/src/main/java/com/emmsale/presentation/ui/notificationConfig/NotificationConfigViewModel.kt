package com.emmsale.presentation.ui.notificationConfig

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.emmsale.model.Config
import com.emmsale.model.EventTag
import com.emmsale.data.repository.interfaces.ConfigRepository
import com.emmsale.data.repository.interfaces.EventTagRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.base.RefreshableViewModel
import com.emmsale.presentation.common.firebase.analytics.logChangeConfig
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.livedata.SingleLiveEvent
import com.emmsale.presentation.ui.notificationConfig.uiState.NotificationConfigUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationConfigViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val eventTagRepository: EventTagRepository,
    private val configRepository: ConfigRepository,
) : RefreshableViewModel() {

    private val uid: Long by lazy { tokenRepository.getMyUid()!! }

    private val _notificationConfig: NotNullMutableLiveData<Config> = NotNullMutableLiveData(
        Config(
            isNotificationReceive = false,
            isCommentNotificationReceive = false,
            isInterestEventNotificationReceive = false,
            isAutoLogin = false,
            isMessageNotificationReceive = false,
        ),
    )
    val notificationConfig: NotNullLiveData<Config> = _notificationConfig

    private val _notificationTags = NotNullMutableLiveData(listOf<EventTag>())
    val notificationTags: NotNullLiveData<List<EventTag>> = _notificationTags

    private val _uiEvent = SingleLiveEvent<NotificationConfigUiEvent>()
    val uiEvent: LiveData<NotificationConfigUiEvent> = _uiEvent

    init {
        fetchNotificationTags()
        fetchNotificationConfig()
    }

    fun fetchNotificationTags(): Job = fetchData(
        fetchData = { eventTagRepository.getInterestEventTags(uid) },
        onSuccess = { _notificationTags.value = it },
    )

    private fun fetchNotificationConfig(): Job = viewModelScope.launch {
        _notificationConfig.value = configRepository.getConfig()
    }

    override fun refresh(): Job {
        fetchNotificationConfig()
        return refreshNotificationTags()
    }

    private fun refreshNotificationTags(): Job = refreshData(
        refresh = { eventTagRepository.getInterestEventTags(uid) },
        onSuccess = { _notificationTags.value = it },
    )

    fun setAllNotificationReceiveConfig(isReceive: Boolean): Job = viewModelScope.launch {
        configRepository.saveAllNotificationReceiveConfig(isReceive)
        fetchNotificationConfig()
        logChangeConfig("notification_receive", isReceive)
    }

    fun setCommentNotificationReceiveConfig(isReceive: Boolean): Job = viewModelScope.launch {
        configRepository.saveCommentNotificationReceiveConfig(isReceive)
        fetchNotificationConfig()
    }

    fun setInterestEventNotificationReceiveConfig(isReceive: Boolean): Job = viewModelScope.launch {
        configRepository.saveInterestEventNotificationReceiveConfig(isReceive)
        fetchNotificationConfig()
    }

    fun setMessageNotificationReceiveConfig(isReceive: Boolean): Job = viewModelScope.launch {
        configRepository.saveMessageNotificationReceiveConfig(isReceive)
        fetchNotificationConfig()
    }

    fun removeNotificationTag(eventTagId: Long): Job = command(
        command = {
            val newTags = _notificationTags.value.filter { it.id != eventTagId }
            eventTagRepository.updateInterestEventTags(newTags)
        },
        onSuccess = {
            _notificationTags.value = _notificationTags.value.filter { it.id != eventTagId }
        },
        onFailure = { _, _ -> _uiEvent.value = NotificationConfigUiEvent.InterestTagRemoveFail },
    )
}
