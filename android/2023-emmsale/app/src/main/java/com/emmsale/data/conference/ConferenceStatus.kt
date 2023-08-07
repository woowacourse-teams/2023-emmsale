package com.emmsale.data.conference

enum class ConferenceStatus(val text: String) {
    IN_PROGRESS("IN_PROGRESS"),
    SCHEDULED("UPCOMING"),
    ENDED("ENDED"),
    ;

    companion object {
        fun from(status: String): ConferenceStatus = values().find { it.text == status }
            ?: throw IllegalArgumentException("${status}는 올바르지 않은 행사 상태입니다.")
    }
}

fun List<ConferenceStatus>.toTexts(): List<String> = map { it.text }
