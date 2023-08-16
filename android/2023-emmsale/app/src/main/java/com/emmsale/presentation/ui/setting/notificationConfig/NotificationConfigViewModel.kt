package com.emmsale.presentation.ui.setting.notificationConfig

import androidx.lifecycle.ViewModel
import com.emmsale.data.eventTag.EventTagRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory

class NotificationConfigViewModel(
    private val eventTagRepository: EventTagRepository,
) : ViewModel() {
    fun setNotificationReceiveConfig(newState: Boolean) {

    }

    companion object {
        val factory = ViewModelFactory {
            NotificationConfigViewModel(
                eventTagRepository = KerdyApplication.repositoryContainer.eventTagRepository,
            )
        }
    }
}