package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActivityResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("activityType")
    val activityType: ActivityType,
) {
    enum class ActivityType {
        @SerialName("동아리")
        CLUB,

        @SerialName("교육")
        EDUCATION,

        @SerialName("직무")
        INTEREST_FIELD,
    }
}
