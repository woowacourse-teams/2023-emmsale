package com.emmsale.presentation.ui.messageRoomList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.messageRoom.MessageRoomRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.messageRoomList.uistate.MemberRoomListUiState
import kotlinx.coroutines.launch

class MessageRoomListViewModel(
    private val memberRepository: TokenRepository,
    private val messageRoomRepository: MessageRoomRepository,
) : ViewModel(), Refreshable {
    private val _memberRooms = MutableLiveData(MemberRoomListUiState())
    val memberRooms: LiveData<MemberRoomListUiState> = _memberRooms

    init {
        refresh()
    }

    override fun refresh() {
        fetchMessageRooms()
    }

    private fun fetchMessageRooms() {
        val uid = memberRepository.getMyUid() ?: return
        _memberRooms.value = memberRooms.value?.toLoading()

        viewModelScope.launch {
            when (val result = messageRoomRepository.getMessageRooms(uid)) {
                is Success -> {
                    _memberRooms.value = memberRooms.value?.toSuccess(result.data)
                }

                is Failure, NetworkError, is Unexpected -> {
                    _memberRooms.value = memberRooms.value?.toError()
                }
            }
        }
    }

    companion object {
        val factory = ViewModelFactory {
            MessageRoomListViewModel(
                memberRepository = KerdyApplication.repositoryContainer.tokenRepository,
                messageRoomRepository = KerdyApplication.repositoryContainer.messageRoomRepository,
            )
        }
    }
}
