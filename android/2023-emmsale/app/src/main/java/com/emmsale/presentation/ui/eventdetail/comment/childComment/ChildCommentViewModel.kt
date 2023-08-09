package com.emmsale.presentation.ui.eventdetail.comment.childComment

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
import com.emmsale.presentation.ui.eventdetail.comment.childComment.uiState.ChildCommentsUiState
import kotlinx.coroutines.launch

class ChildCommentViewModel(
    private val tokenRepository: TokenRepository,
    private val commentRepository: CommentRepository,
) : ViewModel() {

    private val _childCommentsUiState = NotNullMutableLiveData(ChildCommentsUiState.Loading)
    val childCommentsUiState: NotNullLiveData<ChildCommentsUiState> = _childCommentsUiState

    fun fetchComment(commentId: Long) {
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                changeNotLoginState()
                return@launch
            }

            when (val result = commentRepository.getComment(commentId)) {
                is ApiError, is ApiException -> changeCommentFetchingErrorState()
                is ApiSuccess -> setChildCommentsState(result.data, token.uid)
            }
        }
    }

    fun saveChildComment(content: String, parentCommentId: Long, eventId: Long) {
        changeLoadingState()
        viewModelScope.launch {
            when (commentRepository.saveComment(content, eventId, parentCommentId)) {
                is ApiError -> changeCommentPostingErrorState()
                is ApiException -> changeCommentPostingErrorState()
                is ApiSuccess -> fetchComment(parentCommentId)
            }
        }
    }

    fun deleteComment(commentId: Long, parentCommentId: Long) {
        changeLoadingState()
        viewModelScope.launch {
            when (commentRepository.deleteComment(commentId)) {
                is ApiError -> changeCommentDeletionErrorState()
                is ApiException -> changeCommentDeletionErrorState()
                is ApiSuccess -> fetchComment(parentCommentId)
            }
        }
    }

    private fun setChildCommentsState(comment: Comment, loginMemberId: Long) {
        _childCommentsUiState.value = ChildCommentsUiState.create(comment, loginMemberId)
    }

    private fun changeNotLoginState() {
        _childCommentsUiState.value = _childCommentsUiState.value.copy(isNotLogin = true)
    }

    private fun changeCommentFetchingErrorState() {
        _childCommentsUiState.value = childCommentsUiState.value.copy(
            isLoading = false,
            isCommentsFetchingError = true,
            isCommentPostingError = false,
            isCommentDeletionError = false,
        )
    }

    private fun changeCommentPostingErrorState() {
        _childCommentsUiState.value = childCommentsUiState.value.copy(
            isLoading = false,
            isCommentsFetchingError = false,
            isCommentPostingError = true,
            isCommentDeletionError = false,
        )
    }

    private fun changeCommentDeletionErrorState() {
        _childCommentsUiState.value = childCommentsUiState.value.copy(
            isLoading = false,
            isCommentsFetchingError = false,
            isCommentPostingError = false,
            isCommentDeletionError = true,
        )
    }

    private fun changeLoadingState() {
        _childCommentsUiState.value = childCommentsUiState.value.copy(
            isLoading = true,
            isCommentsFetchingError = false,
            isCommentPostingError = false,
            isCommentDeletionError = false,
        )
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
