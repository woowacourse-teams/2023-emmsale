package com.emmsale.data.message

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Message(
    val senderId: Long,
    val message: String,
    val createdAt: LocalDateTime,
) {
    companion object {
        private const val MESSAGE_DATE_FORMAT = "yyyy:MM:dd:HH:mm:ss"

        fun create(senderId: Long, message: String, createdAt: String): Message = Message(
            senderId,
            message,
            LocalDateTime.parse(createdAt, DateTimeFormatter.ofPattern(MESSAGE_DATE_FORMAT)),
        )
    }
}
