package com.emmsale.presentation.ui.eventdetail.comment

import androidx.lifecycle.ViewModel
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

    companion object {
        val factory = ViewModelFactory {
            CommentViewModel(
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
                commentRepository = KerdyApplication.repositoryContainer.commentRepository,
            )
        }
    }
}
