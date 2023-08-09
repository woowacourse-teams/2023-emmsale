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

    private val _commentsUiState = NotNullMutableLiveData(CommentsUiState.Loading)
    val commentsUiState: NotNullLiveData<CommentsUiState> = _commentsUiState

    fun fetchComments(eventId: Long) {
        changeLoadingState()
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                changeNotLoginState()
                return@launch
            }

            when (val result = commentRepository.getComments(eventId)) {
                is ApiError, is ApiException -> changeCommentFetchingErrorState()
                is ApiSuccess -> setCommentsState(result.data, token.uid)
            }
        }
    }

    fun saveComment(content: String, eventId: Long) {
        changeLoadingState()
        viewModelScope.launch {
            when (commentRepository.saveComment(content, eventId)) {
                is ApiError -> changeCommentPostingErrorState()
                is ApiException -> changeCommentPostingErrorState()
                is ApiSuccess -> fetchComments(eventId)
            }
        }
    }

    fun deleteComment(commentId: Long, eventId: Long) {
        changeLoadingState()
        viewModelScope.launch {
            when (commentRepository.deleteComment(commentId)) {
                is ApiError -> changeCommentDeletionErrorState()
                is ApiException -> changeCommentDeletionErrorState()
                is ApiSuccess -> fetchComments(eventId)
            }
        }
    }

    private fun setCommentsState(comments: List<Comment>, loginMemberId: Long) {
        _commentsUiState.value = CommentsUiState.create(comments, loginMemberId)
    }

    private fun changeNotLoginState() {
        _commentsUiState.value = _commentsUiState.value.copy(isNotLogin = true)
    }

    private fun changeLoadingState() {
        _commentsUiState.value = commentsUiState.value.copy(
            isLoading = true,
            isCommentsFetchingError = false,
            isCommentPostingError = false,
            isCommentDeletionError = false,
        )
    }

    private fun changeCommentFetchingErrorState() {
        _commentsUiState.value = commentsUiState.value.copy(
            isLoading = false,
            isCommentsFetchingError = true,
            isCommentPostingError = false,
            isCommentDeletionError = false,
        )
    }

    private fun changeCommentPostingErrorState() {
        _commentsUiState.value = commentsUiState.value.copy(
            isLoading = false,
            isCommentsFetchingError = false,
            isCommentPostingError = true,
            isCommentDeletionError = false,
        )
    }

    private fun changeCommentDeletionErrorState() {
        _commentsUiState.value = commentsUiState.value.copy(
            isLoading = false,
            isCommentsFetchingError = false,
            isCommentPostingError = false,
            isCommentDeletionError = true,
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
