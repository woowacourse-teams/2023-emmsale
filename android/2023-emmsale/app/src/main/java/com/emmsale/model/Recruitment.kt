package com.emmsale.model

import java.time.LocalDate

data class Recruitment(
    val id: Long = -1,
    val writer: Member = Member(),
    val event: Event = Event(),
    val content: String = "",
    val updatedDate: LocalDate = LocalDate.MAX,
)
