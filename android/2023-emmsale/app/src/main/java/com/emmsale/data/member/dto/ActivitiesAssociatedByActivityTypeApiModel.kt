package com.emmsale.data.member.dto

import com.emmsale.data.activity.Activity1
import com.emmsale.data.activity.ActivityType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActivitiesAssociatedByActivityTypeApiModel(
    @SerialName("activityType")
    val activityType: String,
    @SerialName("memberActivityResponses")
    val memberActivityResponses: List<ActivityApiModel>,
) {
    fun toData(): List<Activity1> {
        return memberActivityResponses.map {
            Activity1(
                id = it.id,
                activityType = getActivityType(),
                name = it.name
            )
        }
    }

    fun getActivityType(): ActivityType =
        when (this.activityType) {
            "동아리" -> ActivityType.CLUB
            "컨퍼런스" -> ActivityType.EVENT
            "교육" -> ActivityType.EDUCATION
            "직무" -> ActivityType.JOB
            else -> throw IllegalStateException("회원의 활동 Json 데이터를 도메인 모델로 매핑하는 데 실패했습니다. 서버와 Api 스펙을 다시 상의해보세요.")
        }
}

@Serializable
data class ActivityApiModel(
    val id: Long,
    val name: String,
)
