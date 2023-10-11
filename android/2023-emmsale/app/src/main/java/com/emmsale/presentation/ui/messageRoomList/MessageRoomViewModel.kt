package com.emmsale.presentation.ui.messageRoomList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.common.retrofit.callAdapter.NetworkError
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.common.retrofit.callAdapter.Unexpected
import com.emmsale.data.repository.interfaces.MessageRoomRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.ui.messageRoomList.uistate.MemberRoomListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageRoomViewModel @Inject constructor(
    private val memberRepository: TokenRepository,
    private val messageRoomRepository: MessageRoomRepository,
) : ViewModel(), Refreshable {
    private val _messageRooms = NotNullMutableLiveData(MemberRoomListUiState())
    val messageRooms: NotNullLiveData<MemberRoomListUiState> = _messageRooms

    init {
        refresh()
    }

    override fun refresh() {
        fetchMessageRooms()
    }

    private fun fetchMessageRooms() {
        val uid = memberRepository.getMyUid() ?: return
        _messageRooms.value = messageRooms.value.toLoading()

        viewModelScope.launch {
            when (val result = messageRoomRepository.getMessageRooms(uid)) {
                is Success -> {
                    _messageRooms.value = messageRooms.value.toSuccess(result.data)
                }

                is Failure, NetworkError, is Unexpected -> {
                    _messageRooms.value = messageRooms.value.toError()
                }
            }
        }
    }
}
