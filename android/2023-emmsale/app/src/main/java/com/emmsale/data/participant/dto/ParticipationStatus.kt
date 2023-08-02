package com.emmsale.data.participant.dto

import kotlinx.serialization.Serializable

@Serializable
data class ParticipationStatus(
    val isParticipated: Boolean,
)
