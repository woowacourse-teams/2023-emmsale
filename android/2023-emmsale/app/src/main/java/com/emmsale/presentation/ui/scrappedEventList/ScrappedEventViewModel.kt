package com.emmsale.presentation.ui.scrappedEventList

import com.emmsale.data.model.Event
import com.emmsale.data.repository.interfaces.EventRepository
import com.emmsale.presentation.base.NetworkViewModel
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class ScrappedEventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
) : NetworkViewModel() {
    private val _scrappedEvents = NotNullMutableLiveData(listOf<Event>())
    val scrappedEvents: NotNullLiveData<List<Event>> = _scrappedEvents

    init {
        fetchScrappedEvents()
    }

    private fun fetchScrappedEvents(): Job = fetchData(
        fetchData = { eventRepository.getScrappedEvents() },
        onSuccess = { _scrappedEvents.value = it },
    )

    override fun refresh(): Job = refreshData(
        refresh = { eventRepository.getScrappedEvents() },
        onSuccess = { _scrappedEvents.value = it },
    )
}
