package com.emmsale.data.messageRoom

import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.message.Message

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
    ): ApiResponse<Unit>
}
