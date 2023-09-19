package com.emmsale.presentation.ui.messageRoomList.uistate

import com.emmsale.data.messageRoom.MessageRoom
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.FetchResultUiState

data class MemberRoomListUiState(
    override val fetchResult: FetchResult = FetchResult.LOADING,
    val messageRooms: List<MessageRoom> = emptyList(),
) : FetchResultUiState() {
    fun toSuccess(
        messageRooms: List<MessageRoom>,
    ): MemberRoomListUiState = copy(
        fetchResult = FetchResult.SUCCESS,
        messageRooms = messageRooms,
    )

    fun toLoading() = copy(fetchResult = FetchResult.LOADING)

    fun toError() = copy(fetchResult = FetchResult.ERROR)
}
