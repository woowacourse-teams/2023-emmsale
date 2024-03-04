package com.emmsale.data.repository.interfaces

import com.emmsale.data.network.callAdapter.ApiResponse
import com.emmsale.model.Message
import com.emmsale.model.MessageRoom

interface MessageRoomRepository {
    suspend fun sendMessage(
        senderId: Long,
        receiverId: Long,
        message: String,
    ): ApiResponse<String>

    suspend fun getMessageRooms(
        memberId: Long,
    ): ApiResponse<List<MessageRoom>>

    suspend fun getMessagesByRoomId(
        roomId: String,
        memberId: Long,
    ): ApiResponse<List<Message>>
}
