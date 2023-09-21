package com.emmsale.data.messageRoom

import com.emmsale.data.apiModel.request.MessageRequest
import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.message.Message
import com.emmsale.data.message.mapper.toData
import com.emmsale.data.messageRoom.dto.MessageResponse
import com.emmsale.data.messageRoom.dto.MessageRoomResponse
import com.emmsale.data.messageRoom.mapper.toData
import com.emmsale.di.modules.other.IoDispatcher
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
            .getMessageRooms(memberId)
            .map(List<MessageRoomResponse>::toData)
    }

    override suspend fun getMessagesByRoomId(
        roomId: String,
        memberId: Long,
    ): ApiResponse<List<Message>> = withContext(dispatcher) {
        messageRoomService
            .getMessagesByRoomId(roomId, memberId)
            .map(List<MessageResponse>::toData)
    }

    override suspend fun getMessagesByMemberIds(
        myUid: Long,
        otherUid: Long,
    ): ApiResponse<List<Message>> = withContext(dispatcher) {
        messageRoomService
            .getMessagesByMemberIds(myUid, otherUid)
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