package com.emmsale.data.eventTag.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InterestEventTagUpdateRequestApiModel(
    @SerialName("tagIds")
    val interestEventTagIds: List<Long>,
)
