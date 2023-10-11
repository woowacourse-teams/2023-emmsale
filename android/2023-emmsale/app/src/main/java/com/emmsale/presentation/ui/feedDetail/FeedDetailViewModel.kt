package com.emmsale.presentation.ui.feedDetail

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
import com.emmsale.data.repository.interfaces.FeedRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.common.Event
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.firebase.analytics.logComment
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.ui.feedDetail.uiState.CommentUiState
import com.emmsale.presentation.ui.feedDetail.uiState.FeedDetailUiEvent
import com.emmsale.presentation.ui.feedDetail.uiState.FeedDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val feedRepository: FeedRepository,
    private val commentRepository: CommentRepository,
    private val tokenRepository: TokenRepository,
) : ViewModel(), Refreshable {
    val feedId = savedStateHandle[KEY_FEED_ID] ?: DEFAULT_FEED_ID

    private val uid: Long by lazy { tokenRepository.getMyUid()!! }

    private val _feedDetail: NotNullMutableLiveData<FeedDetailUiState> =
        NotNullMutableLiveData(FeedDetailUiState.Loading)
    val feedDetail: NotNullLiveData<FeedDetailUiState> = _feedDetail

    val isFeedDetailWrittenByLoginUser: Boolean
        get() = _feedDetail.value.feedDetail.writer.id == uid

    private val _editingCommentId = MutableLiveData<Long?>()
    val editingCommentId: LiveData<Long?> = _editingCommentId
    val editingCommentContent = _editingCommentId.map {
        _feedDetail.value.comments.find { commentUiState -> commentUiState.comment.id == it }?.comment?.content
    }

    private val _uiEvent: NotNullMutableLiveData<Event<FeedDetailUiEvent>> =
        NotNullMutableLiveData(Event(FeedDetailUiEvent.None))
    val uiEvent: NotNullLiveData<Event<FeedDetailUiEvent>> = _uiEvent

    init {
        refresh()
    }

    override fun refresh() {
        fetchFeedDetail()
        fetchComments()
    }

    private fun fetchFeedDetail() {
        viewModelScope.launch {
            when (val result = feedRepository.getFeed(feedId)) {
                is Failure -> {
                    if (result.code == DELETED_FEED_FETCH_ERROR_CODE) {
                        _uiEvent.value = Event(FeedDetailUiEvent.DeletedFeedFetch)
                    } else {
                        _feedDetail.value = _feedDetail.value.copy(fetchResult = FetchResult.ERROR)
                    }
                }

                NetworkError ->
                    _feedDetail.value = _feedDetail.value.copy(fetchResult = FetchResult.ERROR)

                is Success -> _feedDetail.value = _feedDetail.value.copy(
                    fetchResult = FetchResult.SUCCESS,
                    feedDetail = result.data,
                )

                is Unexpected ->
                    _uiEvent.value =
                        Event(FeedDetailUiEvent.UnexpectedError(result.error.toString()))
            }
        }
    }

    private fun fetchComments() {
        viewModelScope.launch {
            when (val result = commentRepository.getComments(feedId)) {
                is Failure -> {}
                NetworkError ->
                    _feedDetail.value = _feedDetail.value.copy(fetchResult = FetchResult.ERROR)

                is Success -> _feedDetail.value = _feedDetail.value.copy(
                    comments = result.data.flatMap { parentComment ->
                        listOf(CommentUiState.create(uid, parentComment)) +
                            parentComment.childComments.map { CommentUiState.create(uid, it) }
                    },
                )

                is Unexpected ->
                    _uiEvent.value =
                        Event(FeedDetailUiEvent.UnexpectedError(result.error.toString()))
            }
        }
    }

    fun deleteFeed() {
        viewModelScope.launch {
            _feedDetail.value = _feedDetail.value.copy(fetchResult = FetchResult.LOADING)
            when (val result = feedRepository.deleteFeed(feedId)) {
                is Failure -> {
                    _uiEvent.value = Event(FeedDetailUiEvent.FeedDeleteFail)
                    _feedDetail.value = _feedDetail.value.copy(fetchResult = FetchResult.SUCCESS)
                }

                NetworkError ->
                    _feedDetail.value = _feedDetail.value.copy(fetchResult = FetchResult.ERROR)

                is Success ->
                    _uiEvent.value = Event(FeedDetailUiEvent.FeedDeleteComplete)

                is Unexpected ->
                    _uiEvent.value =
                        Event(FeedDetailUiEvent.UnexpectedError(result.error.toString()))
            }
        }
    }

    fun saveComment(content: String) {
        viewModelScope.launch {
            _feedDetail.value = _feedDetail.value.copy(fetchResult = FetchResult.LOADING)
            when (val result = commentRepository.saveComment(content, feedId)) {
                is Failure -> {
                    _uiEvent.value = Event(FeedDetailUiEvent.CommentPostFail)
                    _feedDetail.value = _feedDetail.value.copy(fetchResult = FetchResult.SUCCESS)
                    logComment(content, feedId)
                }

                NetworkError ->
                    _feedDetail.value = _feedDetail.value.copy(fetchResult = FetchResult.ERROR)

                is Success -> {
                    refresh()
                    _uiEvent.value = Event(FeedDetailUiEvent.CommentPostComplete)
                }

                is Unexpected ->
                    _uiEvent.value =
                        Event(FeedDetailUiEvent.UnexpectedError(result.error.toString()))
            }
        }
    }

    fun updateComment(commentId: Long, content: String) {
        viewModelScope.launch {
            _feedDetail.value = _feedDetail.value.copy(fetchResult = FetchResult.LOADING)
            when (val result = commentRepository.updateComment(commentId, content)) {
                is Failure -> {
                    _uiEvent.value = Event(FeedDetailUiEvent.CommentUpdateFail)
                    _feedDetail.value = _feedDetail.value.copy(fetchResult = FetchResult.SUCCESS)
                }

                NetworkError ->
                    _feedDetail.value = _feedDetail.value.copy(fetchResult = FetchResult.ERROR)

                is Success -> {
                    _editingCommentId.value = null
                    refresh()
                }

                is Unexpected ->
                    _uiEvent.value =
                        Event(FeedDetailUiEvent.UnexpectedError(result.error.toString()))
            }
        }
    }

    fun deleteComment(commentId: Long) {
        viewModelScope.launch {
            _feedDetail.value = _feedDetail.value.copy(fetchResult = FetchResult.LOADING)
            when (val result = commentRepository.deleteComment(commentId)) {
                is Failure -> {
                    _uiEvent.value = Event(FeedDetailUiEvent.CommentDeleteFail)
                    _feedDetail.value = _feedDetail.value.copy(fetchResult = FetchResult.SUCCESS)
                }

                NetworkError ->
                    _feedDetail.value = _feedDetail.value.copy(fetchResult = FetchResult.ERROR)

                is Success -> refresh()
                is Unexpected ->
                    _uiEvent.value =
                        Event(FeedDetailUiEvent.UnexpectedError(result.error.toString()))
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
            _feedDetail.value = _feedDetail.value.copy(fetchResult = FetchResult.LOADING)
            val authorId =
                _feedDetail.value.comments.find { it.comment.id == commentId }?.comment?.authorId
                    ?: return@launch
            when (val result = commentRepository.reportComment(commentId, authorId, uid)) {
                is Failure -> {
                    if (result.code == REPORT_DUPLICATE_ERROR_CODE) {
                        _uiEvent.value = Event(FeedDetailUiEvent.CommentReportDuplicate)
                    } else {
                        _uiEvent.value = Event(FeedDetailUiEvent.CommentReportFail)
                    }
                    _feedDetail.value = _feedDetail.value.copy(fetchResult = FetchResult.SUCCESS)
                }

                NetworkError ->
                    _feedDetail.value = _feedDetail.value.copy(fetchResult = FetchResult.ERROR)

                is Success -> _uiEvent.value = Event(FeedDetailUiEvent.CommentReportComplete)
                is Unexpected ->
                    _uiEvent.value =
                        Event(FeedDetailUiEvent.UnexpectedError(result.error.toString()))
            }
        }
    }

    companion object {
        const val KEY_FEED_ID: String = "KEY_FEED_ID"
        private const val DEFAULT_FEED_ID: Long = -1

        private const val DELETED_FEED_FETCH_ERROR_CODE = 403
        private const val REPORT_DUPLICATE_ERROR_CODE = 400
    }
}
