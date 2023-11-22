package com.emmsale.data.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Message(
    val senderId: Long,
    val message: String,
    val createdAt: LocalDateTime,
) {
    fun isSameDateTime(other: Message): Boolean {
        return createdAt == other.createdAt
    }

    fun isDifferentSender(other: Message): Boolean {
        return senderId != other.senderId
    }

    fun isDifferentDate(other: Message): Boolean {
        return createdAt.dayOfYear != other.createdAt.dayOfYear
    }

    companion object {
        private const val MESSAGE_DATE_FORMAT = "yyyy:MM:dd:HH:mm:ss"

        fun create(senderId: Long, message: String, createdAt: String): Message = Message(
            senderId,
            message,
            LocalDateTime.parse(createdAt, DateTimeFormatter.ofPattern(MESSAGE_DATE_FORMAT)),
        )
    }
}

data class Message2(
    val id: Long,
    val sender: Member,
    val content: String,
    val createdAt: LocalDateTime,
) {
    fun isSameDateTime(other: Message2): Boolean {
        return createdAt == other.createdAt
    }

    fun isDifferentSender(other: Message2): Boolean {
        return sender.id != other.sender.id
    }

    fun isDifferentDate(other: Message2): Boolean {
        return createdAt.dayOfYear != other.createdAt.dayOfYear
    }

    companion object {
        private const val MESSAGE_DATE_FORMAT = "yyyy:MM:dd:HH:mm:ss"

        fun create(
            id: Long,
            sender: Member,
            content: String,
            createdAt: String,
        ): Message2 = Message2(
            id = id,
            sender = sender,
            content = content,
            LocalDateTime.parse(createdAt, DateTimeFormatter.ofPattern(MESSAGE_DATE_FORMAT)),
        )
    }
}
