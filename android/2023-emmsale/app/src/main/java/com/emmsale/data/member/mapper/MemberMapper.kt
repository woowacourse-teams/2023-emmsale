package com.emmsale.data.member.mapper

import com.emmsale.data.member.Member
import com.emmsale.data.member.dto.MemberApiModel

fun MemberApiModel.toData() = Member(
    id = id,
    name = name,
    description = description,
    imageUrl = imageUrl,
)
