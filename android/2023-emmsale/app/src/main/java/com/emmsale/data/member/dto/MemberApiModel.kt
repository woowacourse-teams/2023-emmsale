package com.emmsale.data.member.dto

import com.emmsale.data.member.Member
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberApiModel(
    @SerialName("name")
    val name: String,
    @SerialName("activityIds")
    val activityIds: List<Long>,
) {
    companion object {
        fun from(member: Member): MemberApiModel = MemberApiModel(
            name = member.name,
            activityIds = member.activityIds,
        )
    }
}
