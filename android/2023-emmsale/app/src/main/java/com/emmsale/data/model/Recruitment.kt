package com.emmsale.data.model

import java.time.LocalDate

data class Recruitment(
    val id: Long,
    val writer: Member,
    val event: Event,
    val content: String,
    val updatedDate: LocalDate,
)
