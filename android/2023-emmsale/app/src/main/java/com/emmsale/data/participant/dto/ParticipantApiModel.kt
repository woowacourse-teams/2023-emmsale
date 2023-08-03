package com.emmsale.data.participant.dto

import com.emmsale.data.participant.Participant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParticipantApiModel(
    @SerialName("id")
    val id: Long,
    @SerialName("memberId")
    val memberId: Long,
    @SerialName("name")
    val name: String,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("description")
    val description: String?,
)

fun List<ParticipantApiModel>.toData(): List<Participant> = map {
    Participant(
        id = it.id,
        memberId = it.memberId,
        name = it.name,
        imageUrl = it.imageUrl,
        description = it.description,
    )
}
