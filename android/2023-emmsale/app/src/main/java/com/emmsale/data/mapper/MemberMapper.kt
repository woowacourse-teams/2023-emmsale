package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.MemberApiModel
import com.emmsale.data.model.Member

fun MemberApiModel.toData() = Member(
    id = id,
    githubUrl = githubUrl,
    name = name,
    description = description,
    imageUrl = imageUrl,
    openProfileUrl = openProfileUrl,
)
