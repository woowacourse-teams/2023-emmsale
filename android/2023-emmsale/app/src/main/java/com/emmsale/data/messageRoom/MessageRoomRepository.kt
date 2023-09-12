package com.emmsale.data.messageRoom

import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.message.Message

interface MessageRoomRepository {
    suspend fun getMessageRooms(
        memberId: Long,
    ): ApiResponse<List<MessageRoom>>

    suspend fun getMessagesByRoomId(
        roomId: Long,
        memberId: Long,
    ): ApiResponse<List<Message>>
}
