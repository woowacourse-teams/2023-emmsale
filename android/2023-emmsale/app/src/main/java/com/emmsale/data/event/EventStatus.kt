package com.emmsale.data.event

enum class EventStatus(val text: String) {
    IN_PROGRESS("진행 중"),
    SCHEDULED("진행 예정"),
    ENDED("종료된 행사");

    companion object {
        fun from(status: String): EventStatus = values().find { it.text == status }
            ?: throw IllegalArgumentException("${status}는 올바르지 않은 행사 상태입니다.")
    }
}
