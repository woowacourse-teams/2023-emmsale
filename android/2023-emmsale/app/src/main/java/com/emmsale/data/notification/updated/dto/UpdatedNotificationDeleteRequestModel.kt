package com.emmsale.data.notification.updated.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdatedNotificationDeleteRequestModel(
    @SerialName("deleteIds")
    val notificationIds: List<Long>,
)
