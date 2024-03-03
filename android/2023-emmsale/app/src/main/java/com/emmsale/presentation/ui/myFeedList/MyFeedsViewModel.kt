package com.emmsale.presentation.ui.myFeedList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.emmsale.model.Feed
import com.emmsale.data.repository.interfaces.FeedRepository
import com.emmsale.presentation.base.RefreshableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class MyFeedsViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
) : RefreshableViewModel() {

    private val _myFeeds: MutableLiveData<List<Feed>> = MutableLiveData(emptyList())
    val myFeeds: LiveData<List<Feed>> = _myFeeds

    init {
        fetchMyFeeds()
    }

    private fun fetchMyFeeds(): Job = fetchData(
        fetchData = { feedRepository.getMyFeeds() },
        onSuccess = {
            _myFeeds.value = it.sortedByDescending { feed -> feed.createdAt }
        },
    )

    override fun refresh(): Job = refreshData(
        refresh = { feedRepository.getMyFeeds() },
        onSuccess = {
            _myFeeds.value = it.sortedByDescending { feed -> feed.createdAt }
        },
    )
}
