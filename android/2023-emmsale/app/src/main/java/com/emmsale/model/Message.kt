package com.emmsale.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Message(
    val id: Long,
    val sender: Member,
    val content: String,
    val createdAt: LocalDateTime,
) {
    fun isSameDateTime(other: Message): Boolean {
        return createdAt == other.createdAt
    }

    fun isDifferentSender(other: Message): Boolean {
        return sender.id != other.sender.id
    }

    fun isDifferentDate(other: Message): Boolean {
        return createdAt.dayOfYear != other.createdAt.dayOfYear
    }

    companion object {
        private const val MESSAGE_DATE_FORMAT = "yyyy:MM:dd:HH:mm:ss"

        fun create(
            id: Long,
            sender: Member,
            content: String,
            createdAt: String,
        ): Message = Message(
            id = id,
            sender = sender,
            content = content,
            LocalDateTime.parse(createdAt, DateTimeFormatter.ofPattern(MESSAGE_DATE_FORMAT)),
        )
    }
}
