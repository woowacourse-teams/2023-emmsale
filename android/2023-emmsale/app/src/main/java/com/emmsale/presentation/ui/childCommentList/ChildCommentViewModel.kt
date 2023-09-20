package com.emmsale.presentation.ui.childCommentList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.repository.interfaces.CommentRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.childCommentList.uiState.ChildCommentsUiEvent
import com.emmsale.presentation.ui.childCommentList.uiState.ChildCommentsUiState
import kotlinx.coroutines.launch

class ChildCommentViewModel(
    private val parentCommentId: Long,
    private val tokenRepository: TokenRepository,
    private val commentRepository: CommentRepository,
) : ViewModel(), Refreshable {

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

    private val _event = MutableLiveData<ChildCommentsUiEvent?>(null)
    val event: LiveData<ChildCommentsUiEvent?> = _event

    override fun refresh() {
        _comments.value = _comments.value.changeToLoadingState()
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _isLogin.value = false
                return@launch
            }

            when (val result = commentRepository.getComment(parentCommentId)) {
                is Failure, NetworkError -> _comments.value = _comments.value.changeToErrorState()
                is Success ->
                    _comments.value =
                        _comments.value.changeChildCommentsState(result.data, token.uid)

                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun saveChildComment(content: String, parentCommentId: Long, feedId: Long) {
        viewModelScope.launch {
            when (val result = commentRepository.saveComment(content, feedId, parentCommentId)) {
                is Failure, NetworkError -> _event.value = ChildCommentsUiEvent.POST_ERROR
                is Success -> refresh()
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun updateComment(commentId: Long, content: String) {
        viewModelScope.launch {
            when (val result = commentRepository.updateComment(commentId, content)) {
                is Failure, NetworkError -> _event.value = ChildCommentsUiEvent.UPDATE_ERROR
                is Success -> {
                    _editingCommentId.value = null
                    refresh()
                }

                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun deleteComment(commentId: Long) {
        viewModelScope.launch {
            when (val result = commentRepository.deleteComment(commentId)) {
                is Failure, NetworkError -> _event.value = ChildCommentsUiEvent.DELETE_ERROR
                is Success -> refresh()
                is Unexpected -> throw Throwable(result.error)
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

    fun reportComment(commentId: Long) {
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _isLogin.value = false
                return@launch
            }
            val authorId =
                (_comments.value.childComments + _comments.value.parentComment).find { it.id == commentId }?.authorId
                    ?: return@launch

            when (val result = commentRepository.reportComment(commentId, authorId, token.uid)) {
                is Failure -> {
                    if (result.code == REPORT_DUPLICATE_ERROR_CODE) {
                        _event.value = ChildCommentsUiEvent.REPORT_DUPLICATE
                    } else {
                        _event.value = ChildCommentsUiEvent.REPORT_ERROR
                    }
                }

                NetworkError -> _event.value = ChildCommentsUiEvent.REPORT_ERROR
                is Success -> _event.value = ChildCommentsUiEvent.REPORT_COMPLETE
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun removeEvent() {
        _event.value = null
    }

    companion object {
        private const val REPORT_DUPLICATE_ERROR_CODE = 400

        fun factory(parentCommentId: Long) = ViewModelFactory {
            ChildCommentViewModel(
                parentCommentId = parentCommentId,
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
                commentRepository = KerdyApplication.repositoryContainer.commentRepository,
            )
        }
    }
}
