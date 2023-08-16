package com.emmsale.presentation.ui.setting.notificationConfig.uistate

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
