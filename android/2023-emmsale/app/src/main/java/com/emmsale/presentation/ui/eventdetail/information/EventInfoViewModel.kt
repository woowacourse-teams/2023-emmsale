package com.emmsale.presentation.ui.eventdetail.information

import androidx.lifecycle.ViewModel
import com.emmsale.data.eventdetail.EventDetailRepository
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData

class EventInfoViewModel(
    private val eventDetailRepository: EventDetailRepository,
) : ViewModel() {
    private val _isScraped: NotNullMutableLiveData<Boolean> = NotNullMutableLiveData(false)
    val isScraped: NotNullLiveData<Boolean> = _isScraped

    fun scrapEvent() {
    }
}
