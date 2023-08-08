package com.emmsale.data.member.mapper

import com.emmsale.data.activity.Activity1
import com.emmsale.data.activity.ActivityType
import com.emmsale.data.member.Member1
import com.emmsale.data.member.dto.ActivitiesAssociatedByActivityTypeApiModel
import com.emmsale.data.member.dto.MemberWithoutActivitiesApiModel

fun MemberWithoutActivitiesApiModel.toData(activities: List<ActivitiesAssociatedByActivityTypeApiModel>): Member1 =
    Member1(
        id = this.id,
        name = this.name,
        description = this.description,
        imageUrl = this.imageUrl,
        activities = activities.toData(),
    )

private fun List<ActivitiesAssociatedByActivityTypeApiModel>.toData(): List<Activity1> =
    this.flatMap { it.toData() }

private fun ActivitiesAssociatedByActivityTypeApiModel.toData(): List<Activity1> =
    memberActivityResponses.map {
        Activity1(
            id = it.id,
            activityType = activityType.toData(),
            name = it.name,
        )
    }

private fun String.toData(): ActivityType =
    when (this) {
        "동아리" -> ActivityType.CLUB
        "교육" -> ActivityType.EDUCATION
        "직무" -> ActivityType.CATEGORY
        else -> throw IllegalStateException("회원의 활동 Json 데이터를 도메인 모델로 매핑하는 데 실패했습니다. 서버와 Api 스펙을 다시 상의해보세요.")
    }
