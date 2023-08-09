package com.emmsale.presentation.ui.eventdetail.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.comment.Comment
import com.emmsale.data.comment.CommentRepository
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.member.Member
import com.emmsale.data.member.MemberRepository
import com.emmsale.data.token.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.eventdetail.comment.uiState.CommentsUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class CommentViewModel(
    private val tokenRepository: TokenRepository,
    private val commentRepository: CommentRepository,
    private val memberRepository: MemberRepository,
) : ViewModel() {

    private val _uiState = NotNullMutableLiveData(CommentsUiState.Loading)
    val uiState: NotNullLiveData<CommentsUiState> = _uiState

    fun fetchComments(eventId: Long) {
        changeLoadingStateOnUi()
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _uiState.postValue(_uiState.value.copy(isNotLogin = true))
                return@launch
            }
            val loginMemberDeferred = async { memberRepository.getMember(token.uid) }
            val commentsDeferred = async { commentRepository.getComments(eventId) }
            val (loginMemberResult, commentsResult) = awaitAll(
                loginMemberDeferred,
                commentsDeferred,
            )
            when (loginMemberResult) {
                is ApiError -> {
                    changeCommentFetchingErrorStateOnUi()
                    return@launch
                }

                is ApiException -> {
                    changeCommentFetchingErrorStateOnUi()
                    return@launch
                }

                else -> {}
            }
            val loginMember = (loginMemberResult as ApiSuccess).data as Member

            @Suppress("UNCHECKED_CAST")
            when (commentsResult) {
                is ApiError -> changeCommentFetchingErrorStateOnUi()
                is ApiException -> changeCommentFetchingErrorStateOnUi()
                is ApiSuccess -> _uiState.value = CommentsUiState.create(
                    comments = commentsResult.data as List<Comment>,
                    loginMember = loginMember,
                )
            }
        }
    }

    fun saveComment(content: String, eventId: Long) {
        changeLoadingStateOnUi()
        viewModelScope.launch {
            when (commentRepository.saveComment(content, eventId)) {
                is ApiError -> changeCommentPostingErrorStateOnUi()
                is ApiException -> changeCommentPostingErrorStateOnUi()
                is ApiSuccess -> fetchComments(eventId)
            }
        }
    }

    fun deleteComment(commentId: Long, eventId: Long) {
        changeLoadingStateOnUi()
        viewModelScope.launch {
            when (commentRepository.deleteComment(commentId)) {
                is ApiError -> changeCommentDeletionErrorStateOnUi()
                is ApiException -> changeCommentDeletionErrorStateOnUi()
                is ApiSuccess -> fetchComments(eventId)
            }
        }
    }

    private fun changeLoadingStateOnUi() {
        _uiState.value = uiState.value.copy(
            isLoading = true,
            isCommentsFetchingError = false,
            isCommentPostingError = false,
            isCommentDeletionError = false,
        )
    }

    private fun changeCommentFetchingErrorStateOnUi() {
        _uiState.value = uiState.value.copy(
            isLoading = false,
            isCommentsFetchingError = true,
            isCommentPostingError = false,
            isCommentDeletionError = false,
        )
    }

    private fun changeCommentPostingErrorStateOnUi() {
        _uiState.value = uiState.value.copy(
            isLoading = false,
            isCommentsFetchingError = false,
            isCommentPostingError = true,
            isCommentDeletionError = false,
        )
    }

    private fun changeCommentDeletionErrorStateOnUi() {
        _uiState.value = uiState.value.copy(
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
                memberRepository = KerdyApplication.repositoryContainer.memberRepository,
            )
        }
    }
}
