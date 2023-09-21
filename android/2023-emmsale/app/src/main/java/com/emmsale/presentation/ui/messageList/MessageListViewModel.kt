package com.emmsale.presentation.ui.messageList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.message.Message
import com.emmsale.data.messageRoom.MessageRoomRepository
import com.emmsale.data.model.Member
import com.emmsale.data.repository.interfaces.MemberRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.Event
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.messageList.uistate.MessageDateUiState
import com.emmsale.presentation.ui.messageList.uistate.MessageListUiEvent
import com.emmsale.presentation.ui.messageList.uistate.MessageListUiEvent.MESSAGE_LIST_FIRST_LOADED
import com.emmsale.presentation.ui.messageList.uistate.MessageListUiEvent.MESSAGE_SENDING
import com.emmsale.presentation.ui.messageList.uistate.MessageListUiEvent.MESSAGE_SENT_FAILED
import com.emmsale.presentation.ui.messageList.uistate.MessageListUiEvent.MESSAGE_SENT_SUCCESS
import com.emmsale.presentation.ui.messageList.uistate.MessageListUiEvent.NOT_FOUND_OTHER_MEMBER
import com.emmsale.presentation.ui.messageList.uistate.MessageUiState
import com.emmsale.presentation.ui.messageList.uistate.MessagesUiState
import com.emmsale.presentation.ui.messageList.uistate.MyMessageUiState
import com.emmsale.presentation.ui.messageList.uistate.OtherMessageUiState
import kotlinx.coroutines.launch

class MessageListViewModel(
    private val roomId: String,
    private val otherUid: Long,
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
    private val messageRoomRepository: MessageRoomRepository,
) : ViewModel() {
    private val myUid: Long = tokenRepository.getMyUid() ?: -1

    private val _messages = NotNullMutableLiveData(MessagesUiState())
    val messages: NotNullLiveData<MessagesUiState> = _messages

    private val _uiEvent = MutableLiveData<Event<MessageListUiEvent>>()
    val uiEvent: LiveData<Event<MessageListUiEvent>> = _uiEvent

    private val _otherMember = MutableLiveData<Member>()
    val otherMember: LiveData<Member> = _otherMember

    init {
        viewModelScope.launch {
            fetchMessages()
            _uiEvent.value = Event(MESSAGE_LIST_FIRST_LOADED)
        }
    }

    fun refreshForScrollDown() {
        viewModelScope.launch {
            fetchMessages()
            _uiEvent.value = Event(MESSAGE_LIST_FIRST_LOADED)
        }
    }

    fun refreshForNotScrollDown() {
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
            else -> _uiEvent.value = Event(NOT_FOUND_OTHER_MEMBER)
        }
    }

    private fun updateMessages(newMessages: List<Message>) {
        _messages.value = messages.value.toSuccess(newMessages.toUiState())
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
        date = createdAt.toString(),
    )

    private fun Message.createChatMessageUiState(
        shouldShowProfile: Boolean = true,
    ): MessageUiState = when (senderId) {
        myUid -> MyMessageUiState.create(this, shouldShowProfile)
        else -> OtherMessageUiState.create(this, otherMember.value!!, shouldShowProfile)
    }

    fun sendMessage(message: String) {
        if (message.isBlank()) return

        _uiEvent.value = Event(MESSAGE_SENDING)
        loading()

        viewModelScope.launch {
            when (messageRoomRepository.sendMessage(myUid, otherUid, message)) {
                is Success -> _uiEvent.value = Event(MESSAGE_SENT_SUCCESS)
                else -> _uiEvent.value = Event(MESSAGE_SENT_FAILED)
            }
        }
    }

    companion object {
        fun factory(roomId: String, otherUid: Long) = ViewModelFactory {
            MessageListViewModel(
                roomId = roomId,
                otherUid = otherUid,
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
                memberRepository = KerdyApplication.repositoryContainer.memberRepository,
                messageRoomRepository = KerdyApplication.repositoryContainer.messageRoomRepository,
            )
        }
    }
}
