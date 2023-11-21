package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.ActivityResponse
import com.emmsale.data.model.Activity
import com.emmsale.data.model.ActivityType

fun List<ActivityResponse>.toData(): List<Activity> = map { it.toData() }

fun ActivityResponse.toData(): Activity = Activity(
    id = id,
    activityType = activityType.toData(),
    name = name,
)

private fun String.toData(): ActivityType = when (this) {
    "동아리" -> ActivityType.CLUB
    "교육" -> ActivityType.EDUCATION
    "직무" -> ActivityType.FIELD
    else -> throw IllegalStateException("회원의 활동 Json 데이터를 도메인 모델로 매핑하는 데 실패했습니다. 서버와 Api 스펙을 다시 상의해보세요.")
}
