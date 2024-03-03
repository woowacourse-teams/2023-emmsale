package com.emmsale.data.mapper

import com.emmsale.data.network.apiModel.response.MemberResponse
import com.emmsale.model.Member

fun MemberResponse.toData() = Member(
    id = id,
    githubUrl = githubUrl,
    name = name,
    description = description,
    profileImageUrl = imageUrl,
    activities = activities.toData().toSet(),
)
