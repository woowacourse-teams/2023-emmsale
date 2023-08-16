package com.emmsale.presentation.ui.eventdetail.comment.childComment

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
import com.emmsale.presentation.ui.eventdetail.comment.childComment.uiState.ChildCommentsUiState
import kotlinx.coroutines.launch

class ChildCommentViewModel(
    private val tokenRepository: TokenRepository,
    private val commentRepository: CommentRepository,
) : ViewModel() {

    private val _isLogin = NotNullMutableLiveData(true)
    val isLogin: NotNullLiveData<Boolean> = _isLogin

    private val _comments = NotNullMutableLiveData(ChildCommentsUiState.FIRST_LOADING)
    val comments: NotNullLiveData<ChildCommentsUiState> = _comments

    private val _editingCommentId = MutableLiveData<Long?>()
    val editingCommentId: LiveData<Long?> = _editingCommentId
    val editingCommentContent = _editingCommentId.map {
        (_comments.value.childComments + _comments.value.parentComment)
            .find { comment -> comment.id == it }
            ?.content
    }

    fun fetchComment(commentId: Long) {
        _comments.value = _comments.value.changeToLoadingState()
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _isLogin.value = false
                return@launch
            }

            when (val result = commentRepository.getComment(commentId)) {
                is ApiError, is ApiException ->
                    _comments.value = _comments.value.changeToFetchingErrorState()

                is ApiSuccess ->
                    _comments.value =
                        _comments.value.changeChildCommentsState(result.data, token.uid)
            }
        }
    }

    fun saveChildComment(content: String, parentCommentId: Long, eventId: Long) {
        _comments.value = _comments.value.changeToLoadingState()
        viewModelScope.launch {
            when (commentRepository.saveComment(content, eventId, parentCommentId)) {
                is ApiError, is ApiException ->
                    _comments.value = _comments.value.changeToPostingErrorState()

                is ApiSuccess -> fetchComment(parentCommentId)
            }
        }
    }

    fun updateComment(commentId: Long, content: String, parentCommentId: Long) {
        viewModelScope.launch {
            when (commentRepository.updateComment(commentId, content)) {
                is ApiError, is ApiException -> {}

                is ApiSuccess -> {
                    _editingCommentId.value = null
                    fetchComment(parentCommentId)
                }
            }
        }
    }

    fun deleteComment(commentId: Long, parentCommentId: Long) {
        _comments.value = _comments.value.changeToLoadingState()
        viewModelScope.launch {
            when (commentRepository.deleteComment(commentId)) {
                is ApiError, is ApiException ->
                    _comments.value = _comments.value.changeToDeleteErrorState()

                is ApiSuccess -> fetchComment(parentCommentId)
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

    companion object {
        val factory = ViewModelFactory {
            ChildCommentViewModel(
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
                commentRepository = KerdyApplication.repositoryContainer.commentRepository,
            )
        }
    }
}
