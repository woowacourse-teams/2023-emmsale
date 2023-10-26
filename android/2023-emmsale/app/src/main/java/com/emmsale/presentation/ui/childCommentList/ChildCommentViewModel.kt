package com.emmsale.presentation.ui.childCommentList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import com.emmsale.data.repository.interfaces.CommentRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.base.BaseViewModel
import com.emmsale.presentation.common.Event
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.ui.childCommentList.uiState.ChildCommentsUiEvent
import com.emmsale.presentation.ui.childCommentList.uiState.ChildCommentsUiState
import com.emmsale.presentation.ui.feedDetail.uiState.CommentUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChildCommentViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val tokenRepository: TokenRepository,
    private val commentRepository: CommentRepository,
) : BaseViewModel(), Refreshable {

    private val parentCommentId = stateHandle.get<Long>(KEY_PARENT_COMMENT_ID)!!

    val feedId = stateHandle.get<Long>(KEY_FEED_ID)!!
    private val uid: Long by lazy { tokenRepository.getMyUid()!! }

    private val _comments = NotNullMutableLiveData(ChildCommentsUiState.Loading)
    val comments: NotNullLiveData<ChildCommentsUiState> = _comments

    private val _editingCommentId = MutableLiveData<Long?>()
    val editingCommentId: LiveData<Long?> = _editingCommentId

    val editingCommentContent: LiveData<String?> = _editingCommentId.map {
        _comments.value.comments
            .find { commentUiState -> commentUiState.comment.id == it }
            ?.comment
            ?.content
    }

    private val _uiEvent: NotNullMutableLiveData<Event<ChildCommentsUiEvent>> =
        NotNullMutableLiveData(Event(ChildCommentsUiEvent.None))
    val uiEvent: NotNullLiveData<Event<ChildCommentsUiEvent>> = _uiEvent

    override fun refresh() {
        handleResponse(
            getResult = { commentRepository.getComment(parentCommentId) },
            onSuccess = {
                val comments = listOf(CommentUiState.create(uid, it)) +
                    it.childComments.map { childComment ->
                        CommentUiState.create(uid, childComment)
                    }
                _comments.value = _comments.value.copy(comments = comments)
            },
            onFailure = { _, _ -> },
        )
    }

    fun saveChildComment(content: String) {
        handleResponse(
            getResult = { commentRepository.saveComment(content, feedId, parentCommentId) },
            onSuccess = {
                refresh()
                _uiEvent.value = Event(ChildCommentsUiEvent.CommentPostComplete)
            },
            onFailure = { _, _ -> _uiEvent.value = Event(ChildCommentsUiEvent.CommentPostFail) },
        )
    }

    fun updateComment(commentId: Long, content: String) {
        handleResponse(
            getResult = { commentRepository.updateComment(commentId, content) },
            onSuccess = {
                _editingCommentId.value = null
                refresh()
            },
            onFailure = { _, _ -> _uiEvent.value = Event(ChildCommentsUiEvent.CommentUpdateFail) },
        )
    }

    fun deleteComment(commentId: Long) {
        handleResponse(
            getResult = { commentRepository.deleteComment(commentId) },
            onSuccess = { refresh() },
            onFailure = { _, _ -> _uiEvent.value = Event(ChildCommentsUiEvent.CommentDeleteFail) },
        )
    }

    fun setEditMode(isEditMode: Boolean, commentId: Long = -1) {
        if (!isEditMode) {
            _editingCommentId.value = null
            return
        }
        _editingCommentId.value = commentId
    }

    fun reportComment(commentId: Long) {
        val authorId =
            _comments.value.comments.find { it.comment.id == commentId }!!.comment.authorId

        handleResponse(
            getResult = { commentRepository.reportComment(commentId, authorId, uid) },
            onSuccess = { _uiEvent.value = Event(ChildCommentsUiEvent.CommentReportComplete) },
            onFailure = { code, _ ->
                if (code == REPORT_DUPLICATE_ERROR_CODE) {
                    _uiEvent.value = Event(ChildCommentsUiEvent.CommentReportDuplicate)
                } else {
                    _uiEvent.value = Event(ChildCommentsUiEvent.CommentReportFail)
                }
            },
        )
    }

    override fun changeToLoadingState() {
        _comments.value = _comments.value.copy(fetchResult = FetchResult.LOADING)
    }

    override fun changeToNetworkErrorState() {
        _comments.value = _comments.value.copy(fetchResult = FetchResult.ERROR)
    }

    override fun changeToSuccessState() {
        _comments.value = _comments.value.copy(fetchResult = FetchResult.SUCCESS)
    }

    override fun onUnexpected(throwable: Throwable?) {
        _uiEvent.value = Event(ChildCommentsUiEvent.UnexpectedError(throwable.toString()))
    }

    companion object {
        const val KEY_FEED_ID = "KEY_FEED_ID"
        const val KEY_PARENT_COMMENT_ID = "KEY_PARENT_COMMENT_ID"

        private const val REPORT_DUPLICATE_ERROR_CODE = 400
    }
}
