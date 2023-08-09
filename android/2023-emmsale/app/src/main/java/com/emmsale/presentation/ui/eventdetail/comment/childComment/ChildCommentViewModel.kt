package com.emmsale.presentation.ui.eventdetail.comment.childComment

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
import com.emmsale.presentation.ui.eventdetail.comment.childComment.uiState.ChildCommentsScreenUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class ChildCommentViewModel(
    private val tokenRepository: TokenRepository,
    private val commentRepository: CommentRepository,
    private val memberRepository: MemberRepository,
) : ViewModel() {

    private val _uiState = MutableLiveData(ChildCommentsScreenUiState.Loading)
    val uiState: LiveData<ChildCommentsScreenUiState> = _uiState

    fun fetchComment(commentId: Long) {
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _uiState.postValue(_uiState.value!!.copy(isNotLogin = true))
                return@launch
            }
            val loginMemberDeferred = async { memberRepository.getMember(token.uid) }
            val commentDeferred = async { commentRepository.getComment(commentId) }
            val (loginMemberResult, commentResult) = awaitAll(loginMemberDeferred, commentDeferred)
            when (loginMemberResult) {
                is ApiError -> changeErrorUiState(loginMemberResult.message.toString())
                is ApiException -> changeErrorUiState(loginMemberResult.e.message.toString())
                else -> {}
            }
            val loginMember = (loginMemberResult as ApiSuccess).data as Member

            when (commentResult) {
                is ApiError -> changeErrorUiState(commentResult.message.toString())
                is ApiException -> changeErrorUiState(commentResult.e.message.toString())
                is ApiSuccess -> _uiState.postValue(
                    ChildCommentsScreenUiState.create(
                        comment = commentResult.data as Comment,
                        loginMember = loginMember,
                    ),
                )
            }
        }
    }

    fun saveChildComment(content: String, parentCommentId: Long, eventId: Long) {
        changeLoadingUiState()
        viewModelScope.launch {
            when (commentRepository.saveComment(content, eventId, parentCommentId)) {
                is ApiError -> changeErrorUiState("댓글 게시에 실패했습니다.")
                is ApiException -> changeErrorUiState("댓글 게시에 실패했습니다.")
                is ApiSuccess -> fetchComment(parentCommentId)
            }
        }
    }

    fun deleteComment(commentId: Long, parentCommentId: Long) {
        changeLoadingUiState()
        viewModelScope.launch {
            when (commentRepository.deleteComment(commentId)) {
                is ApiError -> changeErrorUiState("댓글 삭제에 실패했습니다.")
                is ApiException -> changeErrorUiState("댓글 삭제에 실패했습니다.")
                is ApiSuccess -> fetchComment(parentCommentId)
            }
        }
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

    private fun changeLoadingUiState() {
        _uiState.postValue(
            uiState.value!!.copy(
                isLoading = true,
                isError = false,
            ),
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
