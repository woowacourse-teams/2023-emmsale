package com.emmsale.presentation.ui.messageRoomList

import com.emmsale.model.MessageRoom
import com.emmsale.data.repository.interfaces.MessageRoomRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.base.RefreshableViewModel
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class MessageRoomViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val messageRoomRepository: MessageRoomRepository,
) : RefreshableViewModel() {

    private val uid: Long by lazy { tokenRepository.getMyUid()!! }

    private val _messageRooms = NotNullMutableLiveData(emptyList<MessageRoom>())
    val messageRooms: NotNullLiveData<List<MessageRoom>> = _messageRooms

    init {
        fetchData(
            fetchData = { messageRoomRepository.getMessageRooms(uid) },
            onSuccess = { _messageRooms.value = it },
        )
    }

    override fun refresh(): Job = refreshData(
        refresh = { messageRoomRepository.getMessageRooms(uid) },
        onSuccess = { _messageRooms.value = it },
    )
}
