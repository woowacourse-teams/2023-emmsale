package com.emmsale.presentation.ui.messageList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.common.retrofit.callAdapter.NetworkError
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.common.retrofit.callAdapter.Unexpected
import com.emmsale.data.model.Member
import com.emmsale.data.model.Message
import com.emmsale.data.repository.interfaces.MemberRepository
import com.emmsale.data.repository.interfaces.MessageRoomRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.base.RefreshableViewModel
import com.emmsale.presentation.common.NetworkUiEvent
import com.emmsale.presentation.common.NetworkUiState
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.livedata.SingleLiveEvent
import com.emmsale.presentation.ui.messageList.uistate.MessageListUiEvent
import com.emmsale.presentation.ui.messageList.uistate.MessagesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
    private val messageRoomRepository: MessageRoomRepository,
) : RefreshableViewModel() {
    val roomId = savedStateHandle[KEY_ROOM_ID] ?: DEFAULT_ROOM_ID
    private val otherUid = savedStateHandle[KEY_OTHER_UID] ?: DEFAULT_OTHER_ID

    private val myUid: Long = tokenRepository.getMyUid() ?: -1

    private val _otherMember = MutableLiveData<Member>()
    val otherMember: LiveData<Member> = _otherMember

    private val _messages = NotNullMutableLiveData(MessagesUiState())
    val messages: NotNullLiveData<MessagesUiState> = _messages

    private val _canSendMessage = NotNullMutableLiveData(true)
    val canSendMessage: NotNullLiveData<Boolean> = _canSendMessage

    private val _uiEvent = SingleLiveEvent<MessageListUiEvent>()
    val uiEvent: LiveData<MessageListUiEvent> = _uiEvent

    init {
        fetchMessages()
    }

    private fun fetchMessages(): Job = viewModelScope.launch {
        changeToLoadingState()
        val (otherMemberResult, messagesResult) = listOf(
            async { memberRepository.getMember(otherUid) },
            async { messageRoomRepository.getMessagesByRoomId(roomId, myUid) },
        ).awaitAll()

        when {
            otherMemberResult is Unexpected -> {
                _networkUiEvent.value =
                    NetworkUiEvent.Unexpected(otherMemberResult.error?.message.toString())
            }

            messagesResult is Unexpected -> {
                _networkUiEvent.value =
                    NetworkUiEvent.Unexpected(messagesResult.error?.message.toString())
            }

            otherMemberResult is Failure || messagesResult is Failure -> dispatchFetchFailEvent()

            otherMemberResult is NetworkError || messagesResult is NetworkError -> {
                changeToNetworkErrorState()
                return@launch
            }

            otherMemberResult is Success && messagesResult is Success -> {
                _otherMember.value = otherMemberResult.data as Member
                _messages.value =
                    MessagesUiState.create(messagesResult.data as List<Message>, myUid)
            }
        }

        _networkUiState.value = NetworkUiState.NONE
    }

    override fun refresh(): Job = viewModelScope.launch {
        val (otherMemberResult, messagesResult) = listOf(
            async { memberRepository.getMember(otherUid) },
            async { messageRoomRepository.getMessagesByRoomId(roomId, myUid) },
        ).awaitAll()

        when {
            otherMemberResult is Unexpected -> {
                _networkUiEvent.value =
                    NetworkUiEvent.Unexpected(otherMemberResult.error?.message.toString())
            }

            messagesResult is Unexpected -> {
                _networkUiEvent.value =
                    NetworkUiEvent.Unexpected(messagesResult.error?.message.toString())
            }

            otherMemberResult is Failure || messagesResult is Failure -> dispatchFetchFailEvent()

            otherMemberResult is NetworkError || messagesResult is NetworkError -> {
                dispatchNetworkErrorEvent()
                return@launch
            }

            otherMemberResult is Success && messagesResult is Success -> {
                _otherMember.value = otherMemberResult.data as Member
                _messages.value =
                    MessagesUiState.create(messagesResult.data as List<Message>, myUid)
            }
        }

        _networkUiState.value = NetworkUiState.NONE
    }

    fun sendMessage(message: String): Job = commandAndRefresh(
        command = { messageRoomRepository.sendMessage(myUid, otherUid, message) },
        onSuccess = { _uiEvent.value = MessageListUiEvent.MessageSendComplete },
        onFailure = { _, _ -> _uiEvent.value = MessageListUiEvent.MessageSendFail },
        onStart = { _canSendMessage.value = false },
        onFinish = { _canSendMessage.value = true },
    )

    companion object {
        const val KEY_ROOM_ID = "KEY_ROOM_ID"
        private const val DEFAULT_ROOM_ID = ""

        const val KEY_OTHER_UID = "KEY_OTHER_UID"
        private const val DEFAULT_OTHER_ID = -1L
    }
}
