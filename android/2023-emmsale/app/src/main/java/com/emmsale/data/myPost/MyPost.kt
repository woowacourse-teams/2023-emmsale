package com.emmsale.data.myPost

import java.time.LocalDate

data class MyPost(
    val id: Long,
    val eventId: Long,
    val postId: Long,
    val eventName: String? = null,
    val content: String? = null,
    val updatedAt: LocalDate? = null,
)
