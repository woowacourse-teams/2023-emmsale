package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportApiModel(
    @SerialName("id")
    val id: Long,
    @SerialName("reporterId")
    val reporterId: Long,
    @SerialName("reportedId")
    val reportedId: Long,
    @SerialName("type")
    val type: String,
    @SerialName("contentId")
    val contentId: Long,
    @SerialName("createdAt")
    val createdAt: String,
)
