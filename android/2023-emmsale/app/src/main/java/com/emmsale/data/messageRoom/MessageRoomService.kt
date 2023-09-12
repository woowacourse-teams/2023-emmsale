package com.emmsale.data.messageRoom

import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.messageRoom.dto.MessageRoomResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MessageRoomService {
    @GET("/rooms/overview")
    suspend fun getMessageRooms(
        @Query("member-id") memberId: Long,
    ): ApiResponse<List<MessageRoomResponse>>
}
