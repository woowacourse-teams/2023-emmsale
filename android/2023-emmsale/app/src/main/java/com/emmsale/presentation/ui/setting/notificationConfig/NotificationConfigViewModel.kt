package com.emmsale.presentation.ui.setting.notificationConfig

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.config.ConfigRepository
import com.emmsale.data.eventTag.EventTagRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.setting.notificationConfig.uistate.NotificationConfigUiState
import kotlinx.coroutines.launch

class NotificationConfigViewModel(
    private val eventTagRepository: EventTagRepository,
    private val configRepository: ConfigRepository,
) : ViewModel() {
    private val _notificationConfig = NotNullMutableLiveData(NotificationConfigUiState())
    val notificationConfig: NotNullLiveData<NotificationConfigUiState> = _notificationConfig

    init {
        viewModelScope.launch {
            val config = configRepository.getConfig()
            _notificationConfig.value = NotificationConfigUiState.from(config)
        }
    }

    fun setNotificationReceiveConfig(isReceive: Boolean) {
        viewModelScope.launch {
            configRepository.saveNotificationReceiveConfig(isReceive)
        }
    }

    companion object {
        val factory = ViewModelFactory {
            NotificationConfigViewModel(
                eventTagRepository = KerdyApplication.repositoryContainer.eventTagRepository,
                configRepository = KerdyApplication.repositoryContainer.configRepository,
            )
        }
    }
}