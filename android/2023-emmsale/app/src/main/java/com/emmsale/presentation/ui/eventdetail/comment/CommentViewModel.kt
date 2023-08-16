package com.emmsale.presentation.ui.eventdetail.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.emmsale.data.comment.CommentRepository
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.token.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.eventdetail.comment.uiState.CommentsEvent
import com.emmsale.presentation.ui.eventdetail.comment.uiState.CommentsUiState
import kotlinx.coroutines.launch

class CommentViewModel(
    private val tokenRepository: TokenRepository,
    private val commentRepository: CommentRepository,
) : ViewModel() {

    private val _isLogin = NotNullMutableLiveData(true)
    val isLogin: NotNullLiveData<Boolean> = _isLogin

    private val _comments = NotNullMutableLiveData(CommentsUiState.Loading)
    val comments: NotNullLiveData<CommentsUiState> = _comments

    private val _editingCommentId = MutableLiveData<Long?>()
    val editingCommentId: LiveData<Long?> = _editingCommentId
    val editingCommentContent =
        _editingCommentId.map { _comments.value.comments.find { comment -> comment.id == it }?.content }

    private val _event = MutableLiveData<CommentsEvent?>(null)
    val event: LiveData<CommentsEvent?> = _event

    fun fetchComments(eventId: Long) {
        _comments.value = _comments.value.changeToLoadingState()
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _isLogin.value = false
                return@launch
            }

            when (val result = commentRepository.getComments(eventId)) {
                is ApiError, is ApiException ->
                    _comments.value = _comments.value.changeToFetchingErrorState()

                is ApiSuccess ->
                    _comments.value = _comments.value.changeCommentsState(result.data, token.uid)
            }
        }
    }

    fun saveComment(content: String, eventId: Long) {
        _comments.value = _comments.value.changeToLoadingState()
        viewModelScope.launch {
            when (commentRepository.saveComment(content, eventId)) {
                is ApiError, is ApiException ->
                    _comments.value = _comments.value.changeToPostingErrorState()

                is ApiSuccess -> fetchComments(eventId)
            }
        }
    }

    fun updateComment(commentId: Long, content: String, eventId: Long) {
        viewModelScope.launch {
            when (commentRepository.updateComment(commentId, content)) {
                is ApiError, is ApiException ->
                    _comments.value = _comments.value.changeToUpdateErrorState()

                is ApiSuccess -> {
                    _editingCommentId.value = null
                    fetchComments(eventId)
                }
            }
        }
    }

    fun deleteComment(commentId: Long, eventId: Long) {
        _comments.value = _comments.value.changeToLoadingState()
        viewModelScope.launch {
            when (commentRepository.deleteComment(commentId)) {
                is ApiError, is ApiException ->
                    _comments.value = _comments.value.changeToDeleteErrorState()

                is ApiSuccess -> fetchComments(eventId)
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
            when (commentRepository.reportComment(commentId, authorId, token.uid)) {
                is ApiError, is ApiException -> _event.value = CommentsEvent.REPORT_ERROR
                is ApiSuccess -> _event.value = CommentsEvent.REPORT_COMPLETE
            }
        }
    }

    companion object {
        val factory = ViewModelFactory {
            CommentViewModel(
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
                commentRepository = KerdyApplication.repositoryContainer.commentRepository,
            )
        }
    }
}
