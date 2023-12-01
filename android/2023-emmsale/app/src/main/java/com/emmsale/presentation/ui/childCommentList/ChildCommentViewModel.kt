package com.emmsale.presentation.ui.childCommentList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.common.retrofit.callAdapter.NetworkError
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.common.retrofit.callAdapter.Unexpected
import com.emmsale.data.repository.interfaces.CommentRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.common.CommonUiEvent
import com.emmsale.presentation.common.ScreenUiState
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.livedata.SingleLiveEvent
import com.emmsale.presentation.ui.childCommentList.uiState.ChildCommentsUiEvent
import com.emmsale.presentation.ui.childCommentList.uiState.ChildCommentsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates.vetoable

@HiltViewModel
class ChildCommentViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val tokenRepository: TokenRepository,
    private val commentRepository: CommentRepository,
) : ViewModel() {

    var isAlreadyFirstFetched: Boolean by vetoable(false) { _, _, newValue ->
        newValue
    }

    private val parentCommentId = stateHandle.get<Long>(KEY_PARENT_COMMENT_ID)!!

    val feedId = stateHandle.get<Long>(KEY_FEED_ID)!!
    private val uid: Long by lazy { tokenRepository.getMyUid()!! }

    private val _screenUiState = NotNullMutableLiveData(ScreenUiState.LOADING)
    val screenUiState: NotNullLiveData<ScreenUiState> = _screenUiState

    private val _comments = NotNullMutableLiveData(ChildCommentsUiState())
    val comments: NotNullLiveData<ChildCommentsUiState> = _comments

    private val _editingCommentId = MutableLiveData<Long?>()
    val editingCommentId: LiveData<Long?> = _editingCommentId

    val editingCommentContent: LiveData<String?> = _editingCommentId.map {
        _comments.value.comments
            .find { commentUiState -> commentUiState.comment.id == it }
            ?.comment
            ?.content
    }

    private val _commonUiEvent = SingleLiveEvent<CommonUiEvent>()
    val commonUiEvent: LiveData<CommonUiEvent> = _commonUiEvent

    private val _uiEvent = SingleLiveEvent<ChildCommentsUiEvent>()
    val uiEvent: LiveData<ChildCommentsUiEvent> = _uiEvent

    init {
        fetchComments()
    }

    private fun fetchComments(): Job = viewModelScope.launch {
        _screenUiState.value = ScreenUiState.LOADING
        when (val result = commentRepository.getComment(parentCommentId)) {
            is Failure -> _uiEvent.value = ChildCommentsUiEvent.IllegalCommentFetch
            NetworkError -> {
                _screenUiState.value = ScreenUiState.NETWORK_ERROR
                return@launch
            }

            is Success -> _comments.value = ChildCommentsUiState.create(uid, result.data)
            is Unexpected ->
                _commonUiEvent.value = CommonUiEvent.Unexpected(result.error.toString())
        }
        _screenUiState.value = ScreenUiState.NONE
    }

    fun postChildComment(content: String): Job = viewModelScope.launch {
        val loadingJob = launch {
            delay(LOADING_DELAY)
            _screenUiState.value = ScreenUiState.LOADING
        }
        when (val result = commentRepository.saveComment(content, feedId, parentCommentId)) {
            is Failure -> _uiEvent.value = ChildCommentsUiEvent.CommentPostFail
            NetworkError -> _commonUiEvent.value = CommonUiEvent.RequestFailByNetworkError
            is Success -> {
                refresh().join()
                _uiEvent.value = ChildCommentsUiEvent.CommentPostComplete
            }

            is Unexpected ->
                _commonUiEvent.value = CommonUiEvent.Unexpected(result.error.toString())
        }
        loadingJob.cancel()
        _screenUiState.value = ScreenUiState.NONE
    }

    fun updateComment(commentId: Long, content: String): Job = viewModelScope.launch {
        val loadingJob = launch {
            delay(LOADING_DELAY)
            _screenUiState.value = ScreenUiState.LOADING
        }
        when (val result = commentRepository.updateComment(commentId, content)) {
            is Failure -> _uiEvent.value = ChildCommentsUiEvent.CommentUpdateFail
            NetworkError -> _commonUiEvent.value = CommonUiEvent.RequestFailByNetworkError

            is Success -> {
                refresh().join()
                _editingCommentId.value = null
            }

            is Unexpected ->
                _commonUiEvent.value = CommonUiEvent.Unexpected(result.error.toString())
        }
        loadingJob.cancel()
        _screenUiState.value = ScreenUiState.NONE
    }

    fun deleteComment(commentId: Long): Job = viewModelScope.launch {
        val loadingJob = launch {
            delay(LOADING_DELAY)
            _screenUiState.value = ScreenUiState.LOADING
        }
        when (val result = commentRepository.deleteComment(commentId)) {
            is Failure -> _uiEvent.value = ChildCommentsUiEvent.CommentDeleteFail
            NetworkError -> _commonUiEvent.value = CommonUiEvent.RequestFailByNetworkError
            is Success -> refresh().join()
            is Unexpected ->
                _commonUiEvent.value = CommonUiEvent.Unexpected(result.error.toString())
        }
        loadingJob.cancel()
        _screenUiState.value = ScreenUiState.NONE
    }

    fun setEditMode(isEditMode: Boolean, commentId: Long = INVALID_COMMENT_ID) {
        _editingCommentId.value = if (isEditMode) commentId else null
    }

    fun reportComment(commentId: Long): Job = viewModelScope.launch {
        val loadingJob = launch {
            delay(LOADING_DELAY)
            _screenUiState.value = ScreenUiState.LOADING
        }
        val authorId =
            _comments.value.comments.find { it.comment.id == commentId }!!.comment.writer.id
        when (val result = commentRepository.reportComment(commentId, authorId, uid)) {
            is Failure -> {
                if (result.code == REPORT_DUPLICATE_ERROR_CODE) {
                    _uiEvent.value = ChildCommentsUiEvent.CommentReportDuplicate
                } else {
                    _uiEvent.value = ChildCommentsUiEvent.CommentReportFail
                }
            }

            NetworkError -> _commonUiEvent.value = CommonUiEvent.RequestFailByNetworkError
            is Success -> _uiEvent.value = ChildCommentsUiEvent.CommentReportComplete
            is Unexpected ->
                _commonUiEvent.value = CommonUiEvent.Unexpected(result.error.toString())
        }
        loadingJob.cancel()
        _screenUiState.value = ScreenUiState.NONE
    }

    fun refresh(): Job = viewModelScope.launch {
        when (val result = commentRepository.getComment(parentCommentId)) {
            is Failure -> {}
            NetworkError -> {
                _commonUiEvent.value = CommonUiEvent.RequestFailByNetworkError
                return@launch
            }

            is Success -> _comments.value = ChildCommentsUiState.create(uid, result.data)
            is Unexpected ->
                _commonUiEvent.value = CommonUiEvent.Unexpected(result.error.toString())
        }
        _screenUiState.value = ScreenUiState.NONE
    }

    fun highlight(commentId: Long) {
        val comment = _comments.value.comments.find { it.comment.id == commentId } ?: return
        if (comment.isHighlight) return
        _comments.value = _comments.value.highlight(commentId)
    }

    fun unhighlight(commentId: Long) {
        val comment = _comments.value.comments.find { it.comment.id == commentId } ?: return
        if (!comment.isHighlight) return
        _comments.value = _comments.value.unhighlight(commentId)
    }

    companion object {
        const val KEY_FEED_ID = "KEY_FEED_ID"
        const val KEY_PARENT_COMMENT_ID = "KEY_PARENT_COMMENT_ID"

        private const val INVALID_COMMENT_ID: Long = -1

        private const val REPORT_DUPLICATE_ERROR_CODE = 400

        private const val LOADING_DELAY: Long = 1000
    }
}
