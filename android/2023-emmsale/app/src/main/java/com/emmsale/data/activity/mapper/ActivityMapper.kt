package com.emmsale.data.activity.mapper

import com.emmsale.data.activity.Activity
import com.emmsale.data.activity.ActivityType
import com.emmsale.data.activity.dto.ActivitiesApiModel
import com.emmsale.data.activity.dto.ActivityApiModel

fun List<ActivitiesApiModel>.toData(): List<Activity> = flatMap { it.toData() }

fun ActivitiesApiModel.toData(): List<Activity> = activities.map { it.toData(category) }

fun ActivityApiModel.toData(category: String): Activity = Activity(
    id = id,
    activityType = category.toData(),
    name = name,
)

private fun String.toData(): ActivityType = when (this) {
    "동아리" -> ActivityType.CLUB
    "교육" -> ActivityType.EDUCATION
    "직무" -> ActivityType.FIELD
    else -> throw IllegalStateException("회원의 활동 Json 데이터를 도메인 모델로 매핑하는 데 실패했습니다. 서버와 Api 스펙을 다시 상의해보세요.")
}
