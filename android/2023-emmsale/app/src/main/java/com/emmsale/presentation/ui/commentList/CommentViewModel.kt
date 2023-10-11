package com.emmsale.presentation.ui.commentList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.common.retrofit.callAdapter.NetworkError
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.common.retrofit.callAdapter.Unexpected
import com.emmsale.data.repository.interfaces.CommentRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.common.firebase.analytics.logComment
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.ui.commentList.uiState.CommentsUiEvent
import com.emmsale.presentation.ui.commentList.uiState.CommentsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val tokenRepository: TokenRepository,
    private val commentRepository: CommentRepository,
) : ViewModel(), Refreshable {
    val eventId = requireNotNull(stateHandle.get<Long>(KEY_EVENT_ID)) {
        "[ERROR] 컨퍼런스의 댓글 프래그먼트는 컨퍼런스 아이디를 알아야 합니다. 로직을 다시 확인해주세요"
    }

    private val _isLogin = NotNullMutableLiveData(true)
    val isLogin: NotNullLiveData<Boolean> = _isLogin

    private val _comments = NotNullMutableLiveData(CommentsUiState.Loading)
    val comments: NotNullLiveData<CommentsUiState> = _comments

    private val _editingCommentId = MutableLiveData<Long?>()
    val editingCommentId: LiveData<Long?> = _editingCommentId
    val editingCommentContent =
        _editingCommentId.map { _comments.value.comments.find { comment -> comment.id == it }?.content }

    private val _event = MutableLiveData<CommentsUiEvent?>(null)
    val event: LiveData<CommentsUiEvent?> = _event

    override fun refresh() {
        _comments.value = _comments.value.changeToLoadingState()
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _isLogin.value = false
                return@launch
            }

            when (val result = commentRepository.getComments(eventId)) {
                is Failure, NetworkError ->
                    _comments.value = _comments.value.changeToFetchingErrorState()

                is Success ->
                    _comments.value =
                        _comments.value.changeCommentsState(result.data, token.uid)

                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun saveComment(content: String) {
        viewModelScope.launch {
            when (val result = commentRepository.saveComment(content, eventId)) {
                is Failure, NetworkError -> {
                    _event.value = CommentsUiEvent.POST_ERROR
                    logComment(content, eventId)
                }

                is Success -> refresh()
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun updateComment(commentId: Long, content: String) {
        viewModelScope.launch {
            when (val result = commentRepository.updateComment(commentId, content)) {
                is Failure, NetworkError -> _event.value = CommentsUiEvent.UPDATE_ERROR
                is Success -> {
                    _editingCommentId.value = null
                    refresh()
                }

                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun deleteComment(commentId: Long) {
        viewModelScope.launch {
            when (val result = commentRepository.deleteComment(commentId)) {
                is Failure, NetworkError -> _event.value = CommentsUiEvent.DELETE_ERROR
                is Success -> refresh()
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun setEditMode(isEditMode: Boolean, commentId: Long = -1) {
        if (!isEditMode) {
            _editingCommentId.value = null
            return
        }
        _editingCommentId.value = commentId
    }

    fun reportComment(commentId: Long) {
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _isLogin.value = false
                return@launch
            }
            val authorId =
                _comments.value.comments.find { it.id == commentId }?.authorId ?: return@launch
            when (val result = commentRepository.reportComment(commentId, authorId, token.uid)) {
                is Failure -> {
                    if (result.code == REPORT_DUPLICATE_ERROR_CODE) {
                        _event.value = CommentsUiEvent.REPORT_DUPLICATE
                    } else {
                        _event.value = CommentsUiEvent.REPORT_ERROR
                    }
                }

                NetworkError -> _event.value = CommentsUiEvent.REPORT_ERROR
                is Success -> _event.value = CommentsUiEvent.REPORT_COMPLETE
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun removeEvent() {
        _event.value = null
    }

    companion object {
        const val KEY_EVENT_ID = "KEY_EVENT_ID"

        private const val REPORT_DUPLICATE_ERROR_CODE = 400
    }
}
