package com.emmsale.data.notification.recruitment.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecruitmentStatusUpdateRequestModel(
    @SerialName("updatedStatus")
    val updatedStatus: String,
)
