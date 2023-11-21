package com.emmsale.presentation.ui.messageList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.model.Member
import com.emmsale.data.model.Message
import com.emmsale.data.repository.interfaces.MemberRepository
import com.emmsale.data.repository.interfaces.MessageRoomRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.common.UiEvent
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.ui.messageList.uistate.MessageDateUiState
import com.emmsale.presentation.ui.messageList.uistate.MessageListUiEvent
import com.emmsale.presentation.ui.messageList.uistate.MessageListUiEvent.MESSAGE_LIST_FIRST_LOADED
import com.emmsale.presentation.ui.messageList.uistate.MessageListUiEvent.MESSAGE_SENDING
import com.emmsale.presentation.ui.messageList.uistate.MessageListUiEvent.MESSAGE_SENT_FAILED
import com.emmsale.presentation.ui.messageList.uistate.MessageListUiEvent.MESSAGE_SENT_REFRESHED
import com.emmsale.presentation.ui.messageList.uistate.MessageListUiEvent.NOT_FOUND_OTHER_MEMBER
import com.emmsale.presentation.ui.messageList.uistate.MessageUiState
import com.emmsale.presentation.ui.messageList.uistate.MessagesUiState
import com.emmsale.presentation.ui.messageList.uistate.MyMessageUiState
import com.emmsale.presentation.ui.messageList.uistate.OtherMessageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
    private val messageRoomRepository: MessageRoomRepository,
) : ViewModel(), Refreshable {
    val roomId = savedStateHandle[KEY_ROOM_ID] ?: DEFAULT_ROOM_ID
    private val otherUid = savedStateHandle[KEY_OTHER_UID] ?: DEFAULT_OTHER_ID

    private val myUid: Long = tokenRepository.getMyUid() ?: -1

    private val _messages = NotNullMutableLiveData(MessagesUiState())
    val messages: NotNullLiveData<MessagesUiState> = _messages

    private val _uiEvent = MutableLiveData<UiEvent<MessageListUiEvent>>()
    val uiEvent: LiveData<UiEvent<MessageListUiEvent>> = _uiEvent

    private val _otherMember = MutableLiveData<Member>()
    val otherMember: LiveData<Member> = _otherMember

    init {
        viewModelScope.launch {
            fetchMessages()
            _uiEvent.value = UiEvent(MESSAGE_LIST_FIRST_LOADED)
        }
    }

    override fun refresh() {
        viewModelScope.launch { fetchMessages() }
    }

    private suspend fun fetchMessages() {
        loading()
        fetchOtherMember()

        when (val messagesResult = messageRoomRepository.getMessagesByRoomId(roomId, myUid)) {
            is Success -> updateMessages(messagesResult.data)
            else -> _messages.value = messages.value.toError()
        }
    }

    private fun loading() {
        _messages.value = messages.value.toLoading()
    }

    private suspend fun fetchOtherMember() {
        when (val otherMemberResult = memberRepository.getMember(otherUid)) {
            is Success -> _otherMember.value = otherMemberResult.data
            else -> _uiEvent.value = UiEvent(NOT_FOUND_OTHER_MEMBER)
        }
    }

    private fun updateMessages(newMessages: List<Message>) {
        val messagesNotBlank = newMessages
            .filter { it.message.isNotBlank() }
            .toUiState()
        _messages.value = messages.value.toSuccess(messagesNotBlank)
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
    ): MessageUiState = when (senderId) {
        myUid -> MyMessageUiState.create(this, shouldShowProfile)
        else -> OtherMessageUiState.create(this, otherMember.value!!, shouldShowProfile)
    }

    fun sendMessage(message: String) {
        if (message.isBlank()) return

        _uiEvent.value = UiEvent(MESSAGE_SENDING)
        loading()

        viewModelScope.launch {
            when (messageRoomRepository.sendMessage(myUid, otherUid, message)) {
                is Success -> {
                    fetchMessages()
                    _uiEvent.value = UiEvent(MESSAGE_SENT_REFRESHED)
                }

                else -> _uiEvent.value = UiEvent(MESSAGE_SENT_FAILED)
            }
        }
    }

    companion object {
        const val KEY_ROOM_ID = "KEY_ROOM_ID"
        private const val DEFAULT_ROOM_ID = ""

        const val KEY_OTHER_UID = "KEY_OTHER_UID"
        private const val DEFAULT_OTHER_ID = -1L
    }
}
