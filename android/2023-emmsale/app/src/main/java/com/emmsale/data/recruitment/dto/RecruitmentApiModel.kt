package com.emmsale.data.recruitment.dto

import com.emmsale.data.recruitment.Recruitment
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecruitmentApiModel(
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

fun List<RecruitmentApiModel>.toData(): List<Recruitment> = map {
    Recruitment(
        id = it.id,
        memberId = it.memberId,
        name = it.name,
        imageUrl = it.imageUrl,
        description = it.description,
    )
}
