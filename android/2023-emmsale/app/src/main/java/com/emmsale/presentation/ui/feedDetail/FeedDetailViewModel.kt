package com.emmsale.presentation.ui.feedDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.common.retrofit.callAdapter.NetworkError
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.common.retrofit.callAdapter.Unexpected
import com.emmsale.data.model.Comment
import com.emmsale.data.model.Feed
import com.emmsale.data.repository.interfaces.CommentRepository
import com.emmsale.data.repository.interfaces.FeedRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.base.RefreshableViewModel
import com.emmsale.presentation.common.CommonUiEvent
import com.emmsale.presentation.common.ScreenUiState
import com.emmsale.presentation.common.firebase.analytics.logComment
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.livedata.SingleLiveEvent
import com.emmsale.presentation.ui.feedDetail.uiState.CommentUiState
import com.emmsale.presentation.ui.feedDetail.uiState.FeedDetailUiEvent
import com.emmsale.presentation.ui.feedDetail.uiState.FeedDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class FeedDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val feedRepository: FeedRepository,
    private val commentRepository: CommentRepository,
    private val tokenRepository: TokenRepository,
) : RefreshableViewModel() {

    var isAlreadyFirstFetched: Boolean by Delegates.vetoable(false) { _, _, newValue ->
        newValue
    }

    val feedId = savedStateHandle[KEY_FEED_ID] ?: DEFAULT_FEED_ID

    private val uid: Long by lazy { tokenRepository.getMyUid()!! }

    private val _feedDetailUiState = NotNullMutableLiveData(FeedDetailUiState())
    val feedDetailUiState: NotNullLiveData<FeedDetailUiState> = _feedDetailUiState

    private val feed: Feed
        get() = _feedDetailUiState.value.feedUiState.feed

    val commentUiStates: List<CommentUiState>
        get() = _feedDetailUiState.value.commentsUiState.commentUiStates

    val isFeedDetailWrittenByLoginUser: Boolean
        get() = feed.writer.id == uid

    private val _editingCommentId = MutableLiveData<Long?>()
    val editingCommentId: LiveData<Long?> = _editingCommentId
    val editingCommentContent: LiveData<String?> = _editingCommentId.map { commentId ->
        if (commentId == null) null else commentUiStates.find { it.comment.id == commentId }?.comment?.content
    }

    private val _canSubmitComment = NotNullMutableLiveData(true)
    val canSubmitComment: NotNullLiveData<Boolean> = _canSubmitComment

    private val _uiEvent = SingleLiveEvent<FeedDetailUiEvent>()
    val uiEvent: LiveData<FeedDetailUiEvent> = _uiEvent

    private var unhighlightJob: Job? = null

    init {
        fetchFeedAndComments()
    }

    private fun fetchFeedAndComments(): Job = viewModelScope.launch {
        _screenUiState.value = ScreenUiState.LOADING

        val (feedResult, commentResult) = listOf(
            async { feedRepository.getFeed(feedId) },
            async { commentRepository.getComments(feedId) },
        ).awaitAll()

        when {
            feedResult is Unexpected -> {
                _commonUiEvent.value =
                    CommonUiEvent.Unexpected(feedResult.error?.message.toString())
            }

            commentResult is Unexpected -> {
                _commonUiEvent.value =
                    CommonUiEvent.Unexpected(commentResult.error?.message.toString())
            }

            feedResult is Failure && feedResult.code == DELETED_FEED_FETCH_ERROR_CODE -> {
                _uiEvent.value = FeedDetailUiEvent.DeletedFeedFetch
            }

            feedResult is Failure || commentResult is Failure -> {
                dispatchFetchFailEvent()
            }

            feedResult is NetworkError || commentResult is NetworkError -> {
                changeToNetworkErrorState()
                return@launch
            }

            feedResult is Success && commentResult is Success -> {
                val comments = commentResult.data as List<Comment>
                val feed = (feedResult.data as Feed).copy(commentCount = comments.undeletedCount())
                _feedDetailUiState.value = FeedDetailUiState(feed, comments, uid)
            }
        }

        _screenUiState.value = ScreenUiState.NONE
    }

    override fun refresh(): Job = viewModelScope.launch {
        val (feedResult, commentResult) = listOf(
            async { feedRepository.getFeed(feedId) },
            async { commentRepository.getComments(feedId) },
        ).awaitAll()

        when {
            feedResult is Unexpected -> {
                _commonUiEvent.value =
                    CommonUiEvent.Unexpected(feedResult.error?.message.toString())
            }

            commentResult is Unexpected -> {
                _commonUiEvent.value =
                    CommonUiEvent.Unexpected(commentResult.error?.message.toString())
            }

            feedResult is Failure && feedResult.code == DELETED_FEED_FETCH_ERROR_CODE -> {
                _uiEvent.value = FeedDetailUiEvent.DeletedFeedFetch
            }

            feedResult is Failure || commentResult is Failure -> {
                dispatchFetchFailEvent()
            }

            feedResult is NetworkError || commentResult is NetworkError -> {
                dispatchNetworkErrorEvent()
                return@launch
            }

            feedResult is Success && commentResult is Success -> {
                val comments = commentResult.data as List<Comment>
                val feed = (feedResult.data as Feed).copy(commentCount = comments.undeletedCount())
                _feedDetailUiState.value = FeedDetailUiState(feed, comments, uid)
            }
        }
        _screenUiState.value = ScreenUiState.NONE
    }

    private fun List<Comment>.undeletedCount(): Int = commentsCount() - deletedCommentsCount()

    private fun List<Comment>.commentsCount(): Int = this.sumOf { it.childComments.size + 1 }

    private fun List<Comment>.deletedCommentsCount(): Int =
        this.sumOf { parentComment ->
            parentComment.childComments.count { comment -> comment.isDeleted } + if (parentComment.isDeleted) 1 else 0
        }

    fun deleteFeed(): Job = command(
        command = { feedRepository.deleteFeed(feedId) },
        onSuccess = { _uiEvent.value = FeedDetailUiEvent.FeedDeleteComplete },
        onFailure = { _, _ -> _uiEvent.value = FeedDetailUiEvent.FeedDeleteFail },
    )

    fun postComment(content: String): Job = commandAndRefresh(
        command = { commentRepository.saveComment(content, feedId) },
        onSuccess = { _uiEvent.value = FeedDetailUiEvent.CommentPostComplete },
        onFailure = { _, _ ->
            _uiEvent.value = FeedDetailUiEvent.CommentPostFail
            logComment(content, feedId)
        },
        onStart = { _canSubmitComment.value = false },
        onFinish = { _canSubmitComment.value = true },
    )

    fun updateComment(commentId: Long, content: String): Job = commandAndRefresh(
        command = { commentRepository.updateComment(commentId, content) },
        onSuccess = { _editingCommentId.value = null },
        onFailure = { _, _ -> _uiEvent.value = FeedDetailUiEvent.CommentUpdateFail },
        onStart = { _canSubmitComment.value = false },
        onFinish = { _canSubmitComment.value = true },
    )

    fun deleteComment(commentId: Long): Job = commandAndRefresh(
        command = { commentRepository.deleteComment(commentId) },
        onFailure = { _, _ -> _uiEvent.value = FeedDetailUiEvent.CommentDeleteFail },
    )

    fun setEditMode(isEditMode: Boolean, commentId: Long = -1) {
        if (!isEditMode) {
            _editingCommentId.value = null
            return
        }
        _editingCommentId.value = commentId
        highlightComment(commentId)
    }

    fun reportComment(commentId: Long): Job = command(
        command = {
            val authorId = commentUiStates.find { it.comment.id == commentId }
                ?.comment?.writer?.id
                ?: throw IllegalArgumentException("화면에 없는 댓글을 지우려고 시도했습니다. 지우려는 댓글 아이디: $commentId")
            commentRepository.reportComment(commentId, authorId, uid)
        },
        onSuccess = { _uiEvent.value = FeedDetailUiEvent.CommentReportComplete },
        onFailure = { code, _ ->
            if (code == REPORT_DUPLICATE_ERROR_CODE) {
                _uiEvent.value = FeedDetailUiEvent.CommentReportDuplicate
            } else {
                _uiEvent.value = FeedDetailUiEvent.CommentReportFail
            }
        },
    )

    fun highlightComment(commentId: Long) {
        val comment = commentUiStates.find { it.comment.id == commentId } ?: return
        if (comment.isHighlight) return
        _feedDetailUiState.value = _feedDetailUiState.value.highlightComment(commentId)
        _uiEvent.value = FeedDetailUiEvent.CommentHighlight(commentId)

        unhighlightJob?.cancel()
        unhighlightJob = viewModelScope.launch {
            delay(HIGHLIGHT_DURATION)
            _feedDetailUiState.value = _feedDetailUiState.value.unhighlightComment()
        }
    }

    companion object {
        const val KEY_FEED_ID: String = "KEY_FEED_ID"
        private const val DEFAULT_FEED_ID: Long = -1

        private const val DELETED_FEED_FETCH_ERROR_CODE = 403
        private const val REPORT_DUPLICATE_ERROR_CODE = 400

        private const val HIGHLIGHT_DURATION = 2000L
    }
}
