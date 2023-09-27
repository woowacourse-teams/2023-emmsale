package com.emmsale.data.model

import java.time.LocalDateTime

data class MyRecruitmentPost(
    val eventId: Long,
    val postId: Long,
    val eventName: String,
    val content: String? = null,
    val updatedAt: LocalDateTime? = null,
)
