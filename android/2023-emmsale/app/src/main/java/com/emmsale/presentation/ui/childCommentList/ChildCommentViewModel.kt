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
import com.emmsale.presentation.common.Event
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.childCommentList.uiState.ChildCommentsUiEvent
import com.emmsale.presentation.ui.childCommentList.uiState.CommentsUiState
import com.emmsale.presentation.ui.feedDetail.uiState.CommentUiState
import kotlinx.coroutines.launch

class ChildCommentViewModel(
    private val parentCommentId: Long,
    private val tokenRepository: TokenRepository,
    private val commentRepository: CommentRepository,
) : ViewModel(), Refreshable {

    private val uid: Long by lazy { tokenRepository.getMyUid()!! }

    private val _comments = NotNullMutableLiveData(CommentsUiState.Loading)
    val comments: NotNullLiveData<CommentsUiState> = _comments

    private val _editingCommentId = MutableLiveData<Long?>()
    val editingCommentId: LiveData<Long?> = _editingCommentId
    val editingCommentContent = _editingCommentId.map {
        _comments.value.comments.find { commentUiState -> commentUiState.comment.id == it }?.comment?.content
    }

    private val _uiEvent: NotNullMutableLiveData<Event<ChildCommentsUiEvent>> =
        NotNullMutableLiveData(Event(ChildCommentsUiEvent.None))
    val uiEvent: NotNullLiveData<Event<ChildCommentsUiEvent>> = _uiEvent

    override fun refresh() {
        viewModelScope.launch {
            when (val result = commentRepository.getComment(parentCommentId)) {
                is Failure, NetworkError ->
                    _comments.value = _comments.value.copy(fetchResult = FetchResult.ERROR)

                is Success ->
                    _comments.value = _comments.value.copy(
                        fetchResult = FetchResult.SUCCESS,
                        comments = listOf(CommentUiState.create(uid, result.data)) +
                            result.data.childComments.map {
                                CommentUiState.create(uid, it)
                            },
                    )

                is Unexpected ->
                    _uiEvent.value =
                        Event(ChildCommentsUiEvent.UnexpectedError(result.error.toString()))
            }
        }
    }

    fun saveChildComment(content: String, parentCommentId: Long, feedId: Long) {
        viewModelScope.launch {
            _comments.value = _comments.value.copy(fetchResult = FetchResult.LOADING)
            when (val result = commentRepository.saveComment(content, feedId, parentCommentId)) {
                is Failure -> {
                    _uiEvent.value = Event(ChildCommentsUiEvent.CommentPostFail)
                    _comments.value = _comments.value.copy(fetchResult = FetchResult.SUCCESS)
                }

                NetworkError ->
                    _comments.value = _comments.value.copy(fetchResult = FetchResult.ERROR)

                is Success -> {
                    refresh()
                    _uiEvent.value = Event(ChildCommentsUiEvent.CommentPostComplete)
                }

                is Unexpected ->
                    _uiEvent.value =
                        Event(ChildCommentsUiEvent.UnexpectedError(result.error.toString()))
            }
        }
    }

    fun updateComment(commentId: Long, content: String) {
        viewModelScope.launch {
            _comments.value = _comments.value.copy(fetchResult = FetchResult.LOADING)
            when (val result = commentRepository.updateComment(commentId, content)) {
                is Failure -> {
                    _uiEvent.value = Event(ChildCommentsUiEvent.CommentUpdateFail)
                    _comments.value = _comments.value.copy(fetchResult = FetchResult.SUCCESS)
                }

                NetworkError ->
                    _comments.value = _comments.value.copy(fetchResult = FetchResult.ERROR)

                is Success -> {
                    _editingCommentId.value = null
                    refresh()
                }

                is Unexpected ->
                    _uiEvent.value =
                        Event(ChildCommentsUiEvent.UnexpectedError(result.error.toString()))
            }
        }
    }

    fun deleteComment(commentId: Long) {
        viewModelScope.launch {
            _comments.value = _comments.value.copy(fetchResult = FetchResult.LOADING)
            when (val result = commentRepository.deleteComment(commentId)) {
                is Failure -> {
                    _uiEvent.value = Event(ChildCommentsUiEvent.CommentDeleteFail)
                    _comments.value = _comments.value.copy(fetchResult = FetchResult.SUCCESS)
                }

                NetworkError ->
                    _comments.value = _comments.value.copy(fetchResult = FetchResult.ERROR)

                is Success -> refresh()
                is Unexpected ->
                    _uiEvent.value =
                        Event(ChildCommentsUiEvent.UnexpectedError(result.error.toString()))
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
            _comments.value = _comments.value.copy(fetchResult = FetchResult.LOADING)
            val authorId =
                _comments.value.comments.find { it.comment.id == commentId }!!.comment.authorId

            when (val result = commentRepository.reportComment(commentId, authorId, uid)) {
                is Failure -> {
                    if (result.code == REPORT_DUPLICATE_ERROR_CODE) {
                        _uiEvent.value = Event(ChildCommentsUiEvent.CommentReportDuplicate)
                    } else {
                        _uiEvent.value = Event(ChildCommentsUiEvent.CommentReportFail)
                    }
                    _comments.value = _comments.value.copy(fetchResult = FetchResult.SUCCESS)
                }

                NetworkError ->
                    _comments.value = _comments.value.copy(fetchResult = FetchResult.ERROR)

                is Success -> _uiEvent.value = Event(ChildCommentsUiEvent.CommentReportComplete)
                is Unexpected ->
                    _uiEvent.value =
                        Event(ChildCommentsUiEvent.UnexpectedError(result.error.toString()))
            }
        }
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
