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
import com.emmsale.presentation.common.CommonUiEvent
import com.emmsale.presentation.common.ScreenUiState
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.livedata.SingleLiveEvent
import com.emmsale.presentation.ui.messageList.uistate.MessageDateUiState
import com.emmsale.presentation.ui.messageList.uistate.MessageListUiEvent
import com.emmsale.presentation.ui.messageList.uistate.MessageUiState
import com.emmsale.presentation.ui.messageList.uistate.MyMessageUiState
import com.emmsale.presentation.ui.messageList.uistate.OtherMessageUiState
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

    private val _messages = NotNullMutableLiveData(emptyList<MessageUiState>())
    val messages: NotNullLiveData<List<MessageUiState>> = _messages

    private val _canSendMessage = NotNullMutableLiveData(true)
    val canSendMessage: NotNullLiveData<Boolean> = _canSendMessage

    private val _uiEvent = SingleLiveEvent<MessageListUiEvent>()
    val uiEvent: LiveData<MessageListUiEvent> = _uiEvent

    init {
        changeToLoadingState()
        refresh()
        // viewModelScope.launch {
        //
        //     _uiEvent.value = UiEvent(MESSAGE_LIST_FIRST_LOADED)
        // }
    }

    override fun refresh(): Job = viewModelScope.launch {
        val (otherMemberResult, messagesResult) = listOf(
            async { memberRepository.getMember(otherUid) },
            async { messageRoomRepository.getMessagesByRoomId(roomId, myUid) },
        ).awaitAll()

        when {
            otherMemberResult is Unexpected -> {
                _commonUiEvent.value =
                    CommonUiEvent.Unexpected(otherMemberResult.error?.message.toString())
                return@launch
            }

            messagesResult is Unexpected -> {
                _commonUiEvent.value =
                    CommonUiEvent.Unexpected(messagesResult.error?.message.toString())
                return@launch
            }

            otherMemberResult is Failure || messagesResult is Failure -> dispatchFetchFailEvent()

            otherMemberResult is NetworkError || messagesResult is NetworkError -> {
                changeToNetworkErrorState()
                return@launch
            }

            otherMemberResult is Success && messagesResult is Success -> {
                _otherMember.value = otherMemberResult.data as Member
                updateMessages(messagesResult.data as List<Message>)
            }
        }

        _screenUiState.value = ScreenUiState.NONE
    }

    private fun updateMessages(newMessages: List<Message>) {
        val messagesNotBlank = newMessages
            .filter { it.content.isNotBlank() }
            .toUiState()
        _messages.value = messagesNotBlank
    }

    private fun List<Message>.toUiState(): List<MessageUiState> {
        val newMessages = mutableListOf<MessageUiState>()

        forEachIndexed { index, message ->
            when {
                index == 0 -> {
                    newMessages += message.createMessageDateUiState()
                    newMessages += message.createChatMessageUiState()
                }

                message.isDifferentDate(this[index - 1]) -> {
                    newMessages += message.createMessageDateUiState()
                }
            }

            val previousMessage = getOrNull(index - 1) ?: return@forEachIndexed
            val shouldShowProfile = message.shouldShowMemberProfile(previousMessage)
            newMessages += message.createChatMessageUiState(shouldShowProfile)
        }

        return newMessages
    }

    private fun Message.shouldShowMemberProfile(prevMessage: Message): Boolean {
        return isSameDateTime(prevMessage) ||
            isDifferentSender(prevMessage) ||
            isDifferentDate(prevMessage)
    }

    private fun Message.createMessageDateUiState(): MessageDateUiState = MessageDateUiState(
        messageDate = createdAt,
    )

    private fun Message.createChatMessageUiState(
        shouldShowProfile: Boolean = true,
    ): MessageUiState = when (sender.id) {
        myUid -> MyMessageUiState.create(this, shouldShowProfile)
        else -> OtherMessageUiState.create(this, shouldShowProfile)
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
