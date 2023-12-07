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
import com.emmsale.presentation.common.UiEvent
import com.emmsale.presentation.common.firebase.analytics.logComment
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.feedDetail.uiState.CommentsUiState
import com.emmsale.presentation.ui.feedDetail.uiState.FeedDetailUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val feedRepository: FeedRepository,
    private val commentRepository: CommentRepository,
    private val tokenRepository: TokenRepository,
) : RefreshableViewModel() {

    val feedId = savedStateHandle[KEY_FEED_ID] ?: DEFAULT_FEED_ID

    private val uid: Long by lazy { tokenRepository.getMyUid()!! }

    private val _feed = NotNullMutableLiveData(Feed())
    val feed: NotNullLiveData<Feed> = _feed

    private val _comments = NotNullMutableLiveData(CommentsUiState())
    val comments: NotNullLiveData<CommentsUiState> = _comments

    val isFeedDetailWrittenByLoginUser: Boolean
        get() = _feed.value.writer.id == uid

    private val _editingCommentId = MutableLiveData<Long?>()
    val editingCommentId: LiveData<Long?> = _editingCommentId
    val editingCommentContent: LiveData<String?> = _editingCommentId.map { commentId ->
        if (commentId == null) null else _comments.value[commentId]?.comment?.content
    }

    private val _canSubmitComment = NotNullMutableLiveData(true)
    val canSubmitComment: NotNullLiveData<Boolean> = _canSubmitComment

    private val _uiEvent: NotNullMutableLiveData<UiEvent<FeedDetailUiEvent>> =
        NotNullMutableLiveData(UiEvent(FeedDetailUiEvent.None))
    val uiEvent: NotNullLiveData<UiEvent<FeedDetailUiEvent>> = _uiEvent

    init {
        fetchFeedAndComments()
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
                return@launch
            }

            commentResult is Unexpected -> {
                _commonUiEvent.value =
                    CommonUiEvent.Unexpected(commentResult.error?.message.toString())
                return@launch
            }

            feedResult is Failure && feedResult.code == DELETED_FEED_FETCH_ERROR_CODE -> {
                _uiEvent.value = UiEvent(FeedDetailUiEvent.DeletedFeedFetch)
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
                val feed = feedResult.data as Feed
                _feed.value = feed.copy(commentCount = comments.undeletedCount())
                _comments.value = CommentsUiState(uid, comments)
            }
        }
        _screenUiState.value = ScreenUiState.NONE
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
                return@launch
            }

            commentResult is Unexpected -> {
                _commonUiEvent.value =
                    CommonUiEvent.Unexpected(commentResult.error?.message.toString())
                return@launch
            }

            feedResult is Failure && feedResult.code == DELETED_FEED_FETCH_ERROR_CODE -> {
                _uiEvent.value = UiEvent(FeedDetailUiEvent.DeletedFeedFetch)
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
                val feed = feedResult.data as Feed
                _feed.value = feed.copy(commentCount = comments.undeletedCount())
                _comments.value = CommentsUiState(uid, comments)
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
        onSuccess = { _uiEvent.value = UiEvent(FeedDetailUiEvent.FeedDeleteComplete) },
        onFailure = { _, _ -> _uiEvent.value = UiEvent(FeedDetailUiEvent.FeedDeleteFail) },
    )

    fun postComment(content: String): Job = commandAndRefresh(
        command = { commentRepository.saveComment(content, feedId) },
        onSuccess = { _uiEvent.value = UiEvent(FeedDetailUiEvent.CommentPostComplete) },
        onFailure = { _, _ ->
            _uiEvent.value = UiEvent(FeedDetailUiEvent.CommentPostFail)
            logComment(content, feedId)
        },
        onStart = { _canSubmitComment.value = false },
        onFinish = { _canSubmitComment.value = true },
    )

    fun updateComment(commentId: Long, content: String): Job = commandAndRefresh(
        command = { commentRepository.updateComment(commentId, content) },
        onSuccess = { _editingCommentId.value = null },
        onFailure = { _, _ -> _uiEvent.value = UiEvent(FeedDetailUiEvent.CommentUpdateFail) },
        onStart = { _canSubmitComment.value = false },
        onFinish = { _canSubmitComment.value = true },
    )

    fun deleteComment(commentId: Long): Job = commandAndRefresh(
        command = { commentRepository.deleteComment(commentId) },
        onFailure = { _, _ -> _uiEvent.value = UiEvent(FeedDetailUiEvent.CommentDeleteFail) },
    )

    fun setEditMode(isEditMode: Boolean, commentId: Long = -1) {
        if (!isEditMode) {
            _editingCommentId.value = null
            return
        }
        _editingCommentId.value = commentId
    }

    fun reportComment(commentId: Long): Job = command(
        command = {
            val authorId = _comments.value[commentId]
                ?.comment?.writer?.id
                ?: throw IllegalArgumentException("화면에 없는 댓글을 지우려고 시도했습니다. 지우려는 댓글 아이디: $commentId")
            commentRepository.reportComment(commentId, authorId, uid)
        },
        onSuccess = { _uiEvent.value = UiEvent(FeedDetailUiEvent.CommentReportComplete) },
        onFailure = { code, _ ->
            if (code == REPORT_DUPLICATE_ERROR_CODE) {
                _uiEvent.value = UiEvent(FeedDetailUiEvent.CommentReportDuplicate)
            } else {
                _uiEvent.value = UiEvent(FeedDetailUiEvent.CommentReportFail)
            }
        },
    )

    companion object {
        const val KEY_FEED_ID: String = "KEY_FEED_ID"
        private const val DEFAULT_FEED_ID: Long = -1

        private const val DELETED_FEED_FETCH_ERROR_CODE = 403
        private const val REPORT_DUPLICATE_ERROR_CODE = 400
    }
}
