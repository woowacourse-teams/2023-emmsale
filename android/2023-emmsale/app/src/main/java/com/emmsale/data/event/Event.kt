package com.emmsale.data.event

data class Event(
    val id: Int,
    val name: String,
    val startDate: String,
    val endDate: String,
    val status: String,
    val tags: List<String>
)
