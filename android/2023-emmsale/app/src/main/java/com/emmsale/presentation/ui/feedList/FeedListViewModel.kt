package com.emmsale.presentation.ui.feedList

import androidx.lifecycle.SavedStateHandle
import com.emmsale.data.repository.interfaces.FeedRepository
import com.emmsale.model.Feed
import com.emmsale.presentation.base.RefreshableViewModel
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class FeedListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val feedRepository: FeedRepository,
) : RefreshableViewModel() {
    val eventId: Long = savedStateHandle[EVENT_ID_KEY] ?: DEFAULT_EVENT_ID

    private val _feeds = NotNullMutableLiveData(listOf<Feed>())
    val feeds: NotNullLiveData<List<Feed>> = _feeds

    init {
        fetchFeeds()
    }

    private fun fetchFeeds(): Job = fetchData(
        fetchData = { feedRepository.getFeeds(eventId) },
        onSuccess = { _feeds.value = it },
    )

    override fun refresh(): Job = refreshData(
        refresh = { feedRepository.getFeeds(eventId) },
        onSuccess = { _feeds.value = it },
    )

    companion object {
        const val EVENT_ID_KEY = "EVENT_ID_KEY"
        private const val DEFAULT_EVENT_ID = -1L
    }
}
