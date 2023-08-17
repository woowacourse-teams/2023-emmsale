package com.emmsale.presentation.ui.main.setting.notification.uistate

import com.emmsale.data.config.Config

data class NotificationConfigUiState(
    val isNotificationReceive: Boolean = false,
) {
    companion object {
        fun from(config: Config): NotificationConfigUiState =
            NotificationConfigUiState(
                isNotificationReceive = config.isNotificationReceive,
            )
    }
}
