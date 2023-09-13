package com.emmsale.data.apiModel.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdatedNotificationDeleteRequestModel(
    @SerialName("deleteIds")
    val notificationIds: List<Long>,
)
