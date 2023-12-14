package com.emmsale.presentation.ui.childCommentList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.emmsale.data.repository.interfaces.CommentRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.base.RefreshableViewModel
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.livedata.SingleLiveEvent
import com.emmsale.presentation.ui.childCommentList.uiState.ChildCommentsUiEvent
import com.emmsale.presentation.ui.feedDetail.uiState.CommentsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates.vetoable

@HiltViewModel
class ChildCommentsViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val tokenRepository: TokenRepository,
    private val commentRepository: CommentRepository,
) : RefreshableViewModel() {

    var isAlreadyFirstFetched: Boolean by vetoable(false) { _, _, newValue ->
        newValue
    }

    private val parentCommentId = stateHandle.get<Long>(KEY_PARENT_COMMENT_ID)!!

    val feedId = stateHandle.get<Long>(KEY_FEED_ID)!!
    private val uid: Long by lazy { tokenRepository.getMyUid()!! }

    private val _comments = NotNullMutableLiveData(CommentsUiState())
    val comments: NotNullLiveData<CommentsUiState> = _comments

    private val _editingCommentId = MutableLiveData<Long?>()
    val editingCommentId: LiveData<Long?> = _editingCommentId

    val editingCommentContent: LiveData<String?> = _editingCommentId.map { commentId ->
        if (commentId == null) null else _comments.value[commentId]?.comment?.content
    }

    private val _canSubmitComment = NotNullMutableLiveData(true)
    val canSubmitComment: NotNullLiveData<Boolean> = _canSubmitComment

    private var unhighlightJob: Job? = null

    private val _uiEvent = SingleLiveEvent<ChildCommentsUiEvent>()
    val uiEvent: LiveData<ChildCommentsUiEvent> = _uiEvent

    init {
        fetchComments()
    }

    private fun fetchComments(): Job = fetchData(
        fetchData = { commentRepository.getComment(parentCommentId) },
        onSuccess = { _comments.value = CommentsUiState(uid, it) },
        onFailure = { _, _ -> _uiEvent.value = ChildCommentsUiEvent.IllegalCommentFetch },
    )

    fun postChildComment(content: String): Job = commandAndRefresh(
        command = { commentRepository.saveComment(content, feedId, parentCommentId) },
        onSuccess = { _uiEvent.value = ChildCommentsUiEvent.CommentPostComplete },
        onFailure = { _, _ -> _uiEvent.value = ChildCommentsUiEvent.CommentPostFail },
        onStart = { _canSubmitComment.value = false },
        onFinish = { _canSubmitComment.value = true },
    )

    fun updateComment(commentId: Long, content: String): Job = commandAndRefresh(
        command = { commentRepository.updateComment(commentId, content) },
        onSuccess = { _editingCommentId.value = null },
        onFailure = { _, _ -> _uiEvent.value = ChildCommentsUiEvent.CommentUpdateFail },
        onStart = { _canSubmitComment.value = false },
        onFinish = { _canSubmitComment.value = true },
    )

    fun deleteComment(commentId: Long): Job = commandAndRefresh(
        command = { commentRepository.deleteComment(commentId) },
        onFailure = { _, _ -> _uiEvent.value = ChildCommentsUiEvent.CommentDeleteFail },
    )

    fun startEditComment(commentId: Long) {
        _editingCommentId.value = commentId
        unhighlightJob?.cancel()
        _comments.value = _comments.value.highlight(commentId)
    }

    fun cancelEditComment() {
        _editingCommentId.value = null
        _comments.value = _comments.value.unhighlight()
    }

    fun highlightCommentOnFirstEnter(commentId: Long) {
        _comments.value = _comments.value.highlight(commentId)
        unhighlightJob = viewModelScope.launch {
            delay(COMMENT_HIGHLIGHTING_DURATION_ON_FIRST_ENTER)
            _comments.value = _comments.value.unhighlight()
        }
    }

    fun reportComment(commentId: Long): Job = command(
        command = {
            val authorId = _comments.value.commentUiStates
                .find { it.comment.id == commentId }!!
                .comment.writer.id
            commentRepository.reportComment(commentId, authorId, uid)
        },
        onSuccess = { _uiEvent.value = ChildCommentsUiEvent.CommentReportComplete },
        onFailure = { code, _ ->
            if (code == REPORT_DUPLICATE_ERROR_CODE) {
                _uiEvent.value = ChildCommentsUiEvent.CommentReportDuplicate
            } else {
                _uiEvent.value = ChildCommentsUiEvent.CommentReportFail
            }
        },
    )

    override fun refresh(): Job = refreshData(
        refresh = { commentRepository.getComment(parentCommentId) },
        onSuccess = { _comments.value = CommentsUiState(uid, it) },
    )

    companion object {
        const val KEY_FEED_ID = "KEY_FEED_ID"
        const val KEY_PARENT_COMMENT_ID = "KEY_PARENT_COMMENT_ID"

        private const val REPORT_DUPLICATE_ERROR_CODE = 400

        private const val COMMENT_HIGHLIGHTING_DURATION_ON_FIRST_ENTER = 2000L
    }
}
