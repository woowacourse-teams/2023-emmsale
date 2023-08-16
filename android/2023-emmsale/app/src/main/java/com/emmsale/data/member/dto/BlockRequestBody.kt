package com.emmsale.data.member.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlockRequestBody(@SerialName("blockMemberId") val blockMemberId: Long)
