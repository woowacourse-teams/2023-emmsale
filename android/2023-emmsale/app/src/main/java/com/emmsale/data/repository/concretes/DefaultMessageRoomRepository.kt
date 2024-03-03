package com.emmsale.data.repository.concretes

import com.emmsale.data.network.apiModel.request.MessageRequest
import com.emmsale.data.network.apiModel.response.MessageResponse
import com.emmsale.data.network.apiModel.response.MessageRoomResponse
import com.emmsale.data.network.callAdapter.ApiResponse
import com.emmsale.data.mapper.toData
import com.emmsale.model.Message
import com.emmsale.model.MessageRoom
import com.emmsale.data.repository.interfaces.MessageRoomRepository
import com.emmsale.data.network.service.MessageRoomService
import com.emmsale.data.network.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultMessageRoomRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val messageRoomService: MessageRoomService,
) : MessageRoomRepository {

    override suspend fun getMessageRooms(
        memberId: Long,
    ): ApiResponse<List<MessageRoom>> = withContext(dispatcher) {
        messageRoomService
            .getMessageRooms2(memberId)
            .map(List<MessageRoomResponse>::toData)
    }

    override suspend fun getMessagesByRoomId(
        roomId: String,
        memberId: Long,
    ): ApiResponse<List<Message>> = withContext(dispatcher) {
        messageRoomService
            .getMessagesByRoomId2(roomId, memberId)
            .map(List<MessageResponse>::toData)
    }

    override suspend fun sendMessage(
        senderId: Long,
        receiverId: Long,
        message: String,
    ): ApiResponse<String> = withContext(dispatcher) {
        messageRoomService
            .sendMessage(MessageRequest(senderId, receiverId, message))
            .map { it.roomId }
    }
}
