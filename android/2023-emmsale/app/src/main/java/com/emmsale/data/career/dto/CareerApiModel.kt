package com.emmsale.data.career.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CareerApiModel(
    @SerialName("activityName")
    val careerTitle: String = "-",
    @SerialName("activityResponses")
    val careerContents: List<CareerContentApiModel> = emptyList()
)

@Serializable
data class CareerContentApiModel(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
)
