package com.emmsale.data.recruitment

import java.time.LocalDate

data class Recruitment(
    val id: Long,
    val memberId: Long,
    val name: String,
    val imageUrl: String,
    val content: String?,
    val updatedDate: LocalDate,
)
