package com.emmsale.presentation.ui.eventdetail.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.comment.Comment
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
        changeToLoadingState()
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                changeToNotLoginState()
                return@launch
            }

            when (val result = commentRepository.getComments(eventId)) {
                is ApiError, is ApiException -> changeCommentFetchingErrorState()
                is ApiSuccess -> setCommentsState(result.data, token.uid)
            }
        }
    }

    fun saveComment(content: String, eventId: Long) {
        changeToLoadingState()
        viewModelScope.launch {
            when (commentRepository.saveComment(content, eventId)) {
                is ApiError -> changeToCommentPostingErrorState()
                is ApiException -> changeToCommentPostingErrorState()
                is ApiSuccess -> fetchComments(eventId)
            }
        }
    }

    fun deleteComment(commentId: Long, eventId: Long) {
        changeToLoadingState()
        viewModelScope.launch {
            when (commentRepository.deleteComment(commentId)) {
                is ApiError -> changeToCommentDeletionErrorState()
                is ApiException -> changeToCommentDeletionErrorState()
                is ApiSuccess -> fetchComments(eventId)
            }
        }
    }

    private fun setCommentsState(comments: List<Comment>, loginMemberId: Long) {
        _comments.value = CommentsUiState.create(comments, loginMemberId)
    }

    private fun changeToNotLoginState() {
        _isLogin.value = false
    }

    private fun changeToLoadingState() {
        _comments.value = comments.value.copy(
            isLoading = true,
            isFetchingError = false,
            isPostingError = false,
            isDeletionError = false,
        )
    }

    private fun changeCommentFetchingErrorState() {
        _comments.value = comments.value.copy(
            isLoading = false,
            isFetchingError = true,
            isPostingError = false,
            isDeletionError = false,
        )
    }

    private fun changeToCommentPostingErrorState() {
        _comments.value = comments.value.copy(
            isLoading = false,
            isFetchingError = false,
            isPostingError = true,
            isDeletionError = false,
        )
    }

    private fun changeToCommentDeletionErrorState() {
        _comments.value = comments.value.copy(
            isLoading = false,
            isFetchingError = false,
            isPostingError = false,
            isDeletionError = true,
        )
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
