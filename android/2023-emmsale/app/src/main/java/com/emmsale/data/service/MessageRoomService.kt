package com.emmsale.data.service

import com.emmsale.data.apiModel.request.MessageRequest
import com.emmsale.data.apiModel.response.MessageResponse
import com.emmsale.data.apiModel.response.MessageRoomResponse
import com.emmsale.data.apiModel.response.MessageSendResponse
import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MessageRoomService {
    @GET("/rooms/overview")
    suspend fun getMessageRooms2(
        @Query("member-id") memberId: Long,
    ): ApiResponse<List<MessageRoomResponse>>

    @GET("/rooms/{roomId}")
    suspend fun getMessagesByRoomId2(
        @Path("roomId") roomId: String,
        @Query("member-id") memberId: Long,
    ): ApiResponse<List<MessageResponse>>

    @POST("/messages")
    suspend fun sendMessage(
        @Body message: MessageRequest,
    ): ApiResponse<MessageSendResponse>
}
