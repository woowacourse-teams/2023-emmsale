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

    private val _isLogin = NotNullMutableLiveData(true)
    val isLogin: NotNullLiveData<Boolean> = _isLogin

    private val _childCommentsUiState = NotNullMutableLiveData(ChildCommentsUiState.Loading)
    val childCommentsUiState: NotNullLiveData<ChildCommentsUiState> = _childCommentsUiState

    fun fetchComment(commentId: Long) {
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                changeToNotLoginState()
                return@launch
            }

            when (val result = commentRepository.getComment(commentId)) {
                is ApiError, is ApiException -> changeToCommentFetchingErrorState()
                is ApiSuccess -> setChildCommentsState(result.data, token.uid)
            }
        }
    }

    fun saveChildComment(content: String, parentCommentId: Long, eventId: Long) {
        changeToLoadingState()
        viewModelScope.launch {
            when (commentRepository.saveComment(content, eventId, parentCommentId)) {
                is ApiError -> changeToCommentPostingErrorState()
                is ApiException -> changeToCommentPostingErrorState()
                is ApiSuccess -> fetchComment(parentCommentId)
            }
        }
    }

    fun deleteComment(commentId: Long, parentCommentId: Long) {
        changeToLoadingState()
        viewModelScope.launch {
            when (commentRepository.deleteComment(commentId)) {
                is ApiError -> changeToCommentDeletionErrorState()
                is ApiException -> changeToCommentDeletionErrorState()
                is ApiSuccess -> fetchComment(parentCommentId)
            }
        }
    }

    private fun setChildCommentsState(comment: Comment, loginMemberId: Long) {
        _childCommentsUiState.value = ChildCommentsUiState.create(comment, loginMemberId)
    }

    private fun changeToNotLoginState() {
        _isLogin.value = false
    }

    private fun changeToCommentFetchingErrorState() {
        _childCommentsUiState.value = childCommentsUiState.value.copy(
            isLoading = false,
            isCommentsFetchingError = true,
            isCommentPostingError = false,
            isCommentDeletionError = false,
        )
    }

    private fun changeToCommentPostingErrorState() {
        _childCommentsUiState.value = childCommentsUiState.value.copy(
            isLoading = false,
            isCommentsFetchingError = false,
            isCommentPostingError = true,
            isCommentDeletionError = false,
        )
    }

    private fun changeToCommentDeletionErrorState() {
        _childCommentsUiState.value = childCommentsUiState.value.copy(
            isLoading = false,
            isCommentsFetchingError = false,
            isCommentPostingError = false,
            isCommentDeletionError = true,
        )
    }

    private fun changeToLoadingState() {
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
