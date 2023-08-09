package com.emmsale.presentation.ui.eventdetail.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import com.emmsale.presentation.ui.eventdetail.comment.uiState.CommentsUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class CommentViewModel(
    private val tokenRepository: TokenRepository,
    private val commentRepository: CommentRepository,
    private val memberRepository: MemberRepository,
) : ViewModel() {

    private val _uiState = MutableLiveData(CommentsUiState.Loading)
    val uiState: LiveData<CommentsUiState> = _uiState

    fun fetchComments(eventId: Long) {
        changeLoadingUiState()
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _uiState.postValue(_uiState.value!!.copy(isNotLogin = true))
                return@launch
            }
            val loginMemberDeferred = async { memberRepository.getMember(token.uid) }
            val commentsDeferred = async { commentRepository.getComments(eventId) }
            val (loginMemberResult, commentsResult) = awaitAll(
                loginMemberDeferred,
                commentsDeferred,
            )
            when (loginMemberResult) {
                is ApiError -> changeErrorUiState(loginMemberResult.message.toString())
                is ApiException -> changeErrorUiState(loginMemberResult.e.message.toString())
                else -> {}
            }
            val loginMember = (loginMemberResult as ApiSuccess).data as Member

            @Suppress("UNCHECKED_CAST")
            when (commentsResult) {
                is ApiError -> changeErrorUiState(commentsResult.message.toString())
                is ApiException -> changeErrorUiState(commentsResult.e.message.toString())
                is ApiSuccess -> _uiState.postValue(
                    CommentsUiState.create(
                        comments = commentsResult.data as List<Comment>,
                        loginMember = loginMember,
                    ),
                )
            }
        }
    }

    fun saveComment(content: String, eventId: Long) {
        changeLoadingUiState()
        viewModelScope.launch {
            when (commentRepository.saveComment(content, eventId)) {
                is ApiError -> changeErrorUiState("댓글 게시에 실패했습니다.")
                is ApiException -> changeErrorUiState("댓글 게시에 실패했습니다.")
                is ApiSuccess -> fetchComments(eventId)
            }
        }
    }

    fun deleteComment(commentId: Long, eventId: Long) {
        changeLoadingUiState()
        viewModelScope.launch {
            when (commentRepository.deleteComment(commentId)) {
                is ApiError -> changeErrorUiState("댓글 삭제에 실패했습니다.")
                is ApiException -> changeErrorUiState("댓글 삭제에 실패했습니다.")
                is ApiSuccess -> fetchComments(eventId)
            }
        }
    }

    private fun changeLoadingUiState() {
        _uiState.postValue(
            uiState.value!!.copy(
                isLoading = true,
                isError = false,
            ),
        )
    }

    private fun changeErrorUiState(errorMessage: String) {
        _uiState.postValue(
            uiState.value!!.copy(
                isLoading = false,
                isError = true,
                errorMessage = errorMessage,
            ),
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
