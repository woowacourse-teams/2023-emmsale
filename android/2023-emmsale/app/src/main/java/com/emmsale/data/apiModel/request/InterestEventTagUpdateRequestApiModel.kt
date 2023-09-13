package com.emmsale.data.apiModel.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InterestEventTagUpdateRequestApiModel(
    @SerialName("tagIds")
    val interestEventTagIds: List<Long>,
)
