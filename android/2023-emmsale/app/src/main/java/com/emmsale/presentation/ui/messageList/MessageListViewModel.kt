package com.emmsale.presentation.ui.messageList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.message.Message
import com.emmsale.data.messageRoom.MessageRoomRepository
import com.emmsale.data.model.Member
import com.emmsale.data.repository.interfaces.MemberRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.Event
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.messageList.uistate.MessageDateUiState
import com.emmsale.presentation.ui.messageList.uistate.MessageListUiEvent
import com.emmsale.presentation.ui.messageList.uistate.MessageUiState
import com.emmsale.presentation.ui.messageList.uistate.MessagesUiState
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MessageListViewModel(
    private val roomId: String,
    private val otherUid: Long,
    private val tokenRepository: TokenRepository,
    private val memberRepository: MemberRepository,
    private val messageRoomRepository: MessageRoomRepository,
) : ViewModel(), Refreshable {
    private val myUid: Long = tokenRepository.getMyUid() ?: -1

    private val _messages = NotNullMutableLiveData(MessagesUiState())
    val messages: NotNullLiveData<MessagesUiState> = _messages

    private val _event = MutableLiveData<Event<MessageListUiEvent>>()
    val uiEvent: LiveData<Event<MessageListUiEvent>> = _event

    private val _otherMember = MutableLiveData<Member>()
    val otherMember: LiveData<Member> = _otherMember

    init {
        refresh()
    }

    override fun refresh() {
        fetchMessages()
    }

    private fun fetchMessages() {
        viewModelScope.launch {
            _messages.value = messages.value.toLoading()
            _otherMember.value = getOtherMember().await()

            when (val result = messageRoomRepository.getMessagesByRoomId(roomId, myUid)) {
                is Success -> _messages.value = messages.value.toSuccess(
                    messages = result.data.toUiState(),
                )

                is Failure, NetworkError, is Unexpected -> {
                    _messages.value = messages.value.toError()
                }
            }
        }
    }

    private fun List<Message>.toUiState(): List<MessageUiState> {
        val myMessages = mutableListOf<MessageUiState>()

        forEachIndexed { index, message ->
            if (index == 0) { // 첫 번째인 경우
                myMessages.add(
                    MessageDateUiState(
                        date = message.createdAt.toString(),
                        createdAt = message.createdAt,
                    ),
                )
                myMessages.add(
                    message.mapToMessageUiState(myUid),
                ) // 메시지 데이터 추가
            } else {
                val prevMessage = myMessages[index - 1]

                var showProfile = message.createdAt == prevMessage.createdAt
                if (prevMessage.createdAt.dayOfYear != message.createdAt.dayOfYear) {
                    myMessages.add(
                        MessageDateUiState(
                            date = message.createdAt.toString(),
                            createdAt = message.createdAt,
                        ),
                    )
                    showProfile = true
                }
                myMessages.add(
                    MessageUiState.from(
                        myUid,
                        message,
                        otherMember.value!!.profileImageUrl,
                        otherMember.value!!.name,
                        showProfile,
                    ),
                )
            }
        }

        return myMessages
    }

    private fun Message.mapToMessageUiState(myUid: Long): MessageUiState {
        val otherMember = otherMember.value!!
        return MessageUiState.from(
            myUid,
            this,
            otherMember.profileImageUrl,
            otherMember.name,
        )
    }

    private fun getOtherMember(): Deferred<Member?> = viewModelScope.async {
        when (val otherMember = memberRepository.getMember(otherUid)) {
            is Success -> otherMember.data
            else -> null
        }
    }

    fun sendMessage(message: String) {
        if (message.isBlank()) return

        _messages.value = messages.value.toLoading()
        viewModelScope.launch {
            when (messageRoomRepository.sendMessage(myUid, otherUid, message)) {
                is Success -> {
                    _event.value = Event(MessageListUiEvent.MESSAGE_SENT)
                    fetchMessages()
                }

                else -> _messages.value = messages.value.toError()
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
