package com.emmsale.data.myPost

import java.time.LocalDate

data class MyPost(
    val eventId: Long,
    val postId: Long,
    val eventName: String,
    val content: String? = null,
    val updatedAt: LocalDate? = null,
)
