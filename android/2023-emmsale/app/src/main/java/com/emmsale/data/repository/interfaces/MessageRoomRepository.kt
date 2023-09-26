package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.model.Message
import com.emmsale.data.model.MessageRoom

interface MessageRoomRepository {
    suspend fun getMessageRooms(
        memberId: Long,
    ): ApiResponse<List<MessageRoom>>

    suspend fun getMessagesByRoomId(
        roomId: String,
        memberId: Long,
    ): ApiResponse<List<Message>>

    suspend fun getMessagesByMemberIds(
        myUid: Long,
        otherUid: Long,
    ): ApiResponse<List<Message>>

    suspend fun sendMessage(
        senderId: Long,
        receiverId: Long,
        message: String,
    ): ApiResponse<String>
}
