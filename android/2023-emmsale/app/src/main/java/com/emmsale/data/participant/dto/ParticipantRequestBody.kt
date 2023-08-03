package com.emmsale.data.participant.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParticipantRequestBody(
    @SerialName("memberId")
    val memberId: Long,
)
