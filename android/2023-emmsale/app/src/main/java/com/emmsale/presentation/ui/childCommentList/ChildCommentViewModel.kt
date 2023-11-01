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
import com.emmsale.presentation.ui.childCommentList.uiState.ChildCommentsUiEvent
import com.emmsale.presentation.ui.childCommentList.uiState.ChildCommentsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class ChildCommentViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val tokenRepository: TokenRepository,
    private val commentRepository: CommentRepository,
) : BaseViewModel() {

    var isAlreadyFirstFetched: Boolean by Delegates.vetoable(false) { _, _, newValue ->
        newValue
    }

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

    init {
        fetchComments()
    }

    private fun fetchComments() {
        requestToNetwork(
            getResult = { commentRepository.getComment(parentCommentId) },
            onSuccess = { _comments.value = ChildCommentsUiState.create(uid, parentComment = it) },
            onFailure = { _, _ ->
                _uiEvent.value = Event(ChildCommentsUiEvent.IllegalCommentFetch)
            },
        )
    }

    fun postChildComment(content: String) {
        commandAndRefresh(
            command = { commentRepository.saveComment(content, feedId, parentCommentId) },
            onSuccess = { _uiEvent.value = Event(ChildCommentsUiEvent.CommentPostComplete) },
            onFailure = { _, _ -> _uiEvent.value = Event(ChildCommentsUiEvent.CommentPostFail) },
            onLoading = { delayLoading() },
        )
    }

    fun updateComment(commentId: Long, content: String) {
        commandAndRefresh(
            command = { commentRepository.updateComment(commentId, content) },
            onSuccess = { _editingCommentId.value = null },
            onFailure = { _, _ -> _uiEvent.value = Event(ChildCommentsUiEvent.CommentUpdateFail) },
            onLoading = { delayLoading() },
        )
    }

    fun deleteComment(commentId: Long) {
        commandAndRefresh(
            command = { commentRepository.deleteComment(commentId) },
            onFailure = { _, _ -> _uiEvent.value = Event(ChildCommentsUiEvent.CommentDeleteFail) },
            onLoading = { delayLoading() },
        )
    }

    fun setEditMode(isEditMode: Boolean, commentId: Long = -1) {
        _editingCommentId.value = if (isEditMode) commentId else null
    }

    fun reportComment(commentId: Long) {
        val authorId =
            _comments.value.comments.find { it.comment.id == commentId }!!.comment.authorId

        requestToNetwork(
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

    private suspend fun delayLoading() {
        delay(LOADING_DELAY)
        changeToLoadingState()
    }

    fun highlight(commentId: Long) {
        _comments.value = _comments.value.highlight(commentId)
    }

    fun unhighlight(commentId: Long) {
        _comments.value = _comments.value.unhighlight(commentId)
    }

    override fun refresh(): Job = requestToNetwork(
        getResult = { commentRepository.getComment(parentCommentId) },
        onSuccess = { _comments.value = ChildCommentsUiState.create(uid, parentComment = it) },
        onFailure = { _, _ -> },
        onLoading = {},
    )

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

    override fun onRequestFailByNetworkError() {
        _uiEvent.value = Event(ChildCommentsUiEvent.RequestFailByNetworkError)
    }

    companion object {
        const val KEY_FEED_ID = "KEY_FEED_ID"
        const val KEY_PARENT_COMMENT_ID = "KEY_PARENT_COMMENT_ID"

        private const val REPORT_DUPLICATE_ERROR_CODE = 400

        private const val LOADING_DELAY: Long = 500
    }
}
