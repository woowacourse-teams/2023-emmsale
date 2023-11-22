package com.emmsale.presentation.ui.feedList

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.repository.interfaces.FeedRepository
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.ui.feedList.uiState.FeedsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val feedRepository: FeedRepository,
) : ViewModel(), Refreshable {
    val eventId: Long = savedStateHandle[EVENT_ID_KEY] ?: DEFAULT_EVENT_ID

    private val _feeds = NotNullMutableLiveData(FeedsUiState())
    val feeds: NotNullLiveData<FeedsUiState> = _feeds

    init {
        refresh()
    }

    override fun refresh() {
        fetchFeeds()
    }

    private fun fetchFeeds() {
        _feeds.value = feeds.value.copy(fetchResult = FetchResult.LOADING)
        viewModelScope.launch {
            when (val result = feedRepository.getFeeds(eventId)) {
                is Success -> _feeds.value = feeds.value.copy(
                    fetchResult = FetchResult.SUCCESS,
                    feeds = result.data,
                )

                else -> _feeds.value = feeds.value.copy(fetchResult = FetchResult.ERROR)
            }
        }
    }

    companion object {
        const val EVENT_ID_KEY = "EVENT_ID_KEY"
        private const val DEFAULT_EVENT_ID = -1L
    }
}
