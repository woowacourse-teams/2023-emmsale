package com.emmsale.presentation.ui.eventdetail.comment.childComment

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
import com.emmsale.presentation.ui.eventdetail.comment.childComment.uiState.ChildCommentsUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class ChildCommentViewModel(
    private val tokenRepository: TokenRepository,
    private val commentRepository: CommentRepository,
    private val memberRepository: MemberRepository,
) : ViewModel() {

    private val _uiState = NotNullMutableLiveData(ChildCommentsUiState.Loading)
    val uiState: NotNullLiveData<ChildCommentsUiState> = _uiState

    fun fetchComment(commentId: Long) {
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _uiState.postValue(_uiState.value.copy(isNotLogin = true))
                return@launch
            }
            val loginMemberDeferred = async { memberRepository.getMember(token.uid) }
            val commentDeferred = async { commentRepository.getComment(commentId) }
            val (loginMemberResult, commentResult) = awaitAll(loginMemberDeferred, commentDeferred)
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

            when (commentResult) {
                is ApiError -> changeCommentFetchingErrorStateOnUi()
                is ApiException -> changeCommentFetchingErrorStateOnUi()
                is ApiSuccess -> _uiState.value = ChildCommentsUiState.create(
                    comment = commentResult.data as Comment,
                    loginMember = loginMember,
                )
            }
        }
    }

    fun saveChildComment(content: String, parentCommentId: Long, eventId: Long) {
        changeLoadingStateOnUi()
        viewModelScope.launch {
            when (commentRepository.saveComment(content, eventId, parentCommentId)) {
                is ApiError -> changeCommentPostingErrorStateOnUi()
                is ApiException -> changeCommentPostingErrorStateOnUi()
                is ApiSuccess -> fetchComment(parentCommentId)
            }
        }
    }

    fun deleteComment(commentId: Long, parentCommentId: Long) {
        changeLoadingStateOnUi()
        viewModelScope.launch {
            when (commentRepository.deleteComment(commentId)) {
                is ApiError -> changeCommentDeletionErrorStateOnUi()
                is ApiException -> changeCommentDeletionErrorStateOnUi()
                is ApiSuccess -> fetchComment(parentCommentId)
            }
        }
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

    private fun changeLoadingStateOnUi() {
        _uiState.value = uiState.value.copy(
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
                memberRepository = KerdyApplication.repositoryContainer.memberRepository,
            )
        }
    }
}
