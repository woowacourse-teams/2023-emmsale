package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import com.emmsale.data.model.Message
import com.emmsale.data.model.Message2
import com.emmsale.data.model.MessageRoom
import com.emmsale.data.model.MessageRoom2

interface MessageRoomRepository {
    suspend fun getMessageRooms(
        memberId: Long,
    ): ApiResponse<List<MessageRoom>>

    suspend fun getMessagesByRoomId(
        roomId: String,
        memberId: Long,
    ): ApiResponse<List<Message>>

    suspend fun sendMessage(
        senderId: Long,
        receiverId: Long,
        message: String,
    ): ApiResponse<String>

    suspend fun getMessageRooms2(
        memberId: Long,
    ): ApiResponse<List<MessageRoom2>>

    suspend fun getMessagesByRoomId2(
        roomId: String,
        memberId: Long,
    ): ApiResponse<List<Message2>>
}
