package com.emmsale.presentation.ui.notificationTagConfig

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.emmsale.data.network.callAdapter.Failure
import com.emmsale.data.network.callAdapter.NetworkError
import com.emmsale.data.network.callAdapter.Success
import com.emmsale.data.network.callAdapter.Unexpected
import com.emmsale.model.EventTag
import com.emmsale.data.repository.interfaces.EventTagRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.base.RefreshableViewModel
import com.emmsale.presentation.common.NetworkUiEvent
import com.emmsale.presentation.common.NetworkUiState
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.livedata.SingleLiveEvent
import com.emmsale.presentation.ui.notificationTagConfig.uiState.NotificationTagConfigUiEvent
import com.emmsale.presentation.ui.notificationTagConfig.uiState.NotificationTagsConfigUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationTagConfigViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val eventTagRepository: EventTagRepository,
) : RefreshableViewModel() {

    private val uid: Long by lazy { tokenRepository.getMyUid()!! }

    private var originNotificationTags: List<EventTag> = emptyList()

    private val _notificationTags = NotNullMutableLiveData(NotificationTagsConfigUiState())
    val notificationTags: NotNullLiveData<NotificationTagsConfigUiState> = _notificationTags

    val isChanged: LiveData<Boolean> = _notificationTags.map { uiState ->
        originNotificationTags != uiState.eventTags
            .filter { it.isChecked }
            .map { it.eventTag }
    }

    private val _uiEvent = SingleLiveEvent<NotificationTagConfigUiEvent>()
    val uiEvent: LiveData<NotificationTagConfigUiEvent> = _uiEvent

    init {
        fetchAll()
    }

    private fun fetchAll(): Job = viewModelScope.launch {
        changeToLoadingState()
        val (eventTagsResult, interestTagsResult) = listOf(
            async { eventTagRepository.getEventTags() },
            async { eventTagRepository.getInterestEventTags(uid) },
        ).awaitAll()

        when {
            eventTagsResult is Unexpected ->
                _networkUiEvent.value = NetworkUiEvent.Unexpected(eventTagsResult.error.toString())

            interestTagsResult is Unexpected ->
                _networkUiEvent.value =
                    NetworkUiEvent.Unexpected(interestTagsResult.error.toString())

            eventTagsResult is Failure || interestTagsResult is Failure -> dispatchFetchFailEvent()
            eventTagsResult is NetworkError || interestTagsResult is NetworkError -> {
                changeToNetworkErrorState()
                return@launch
            }

            eventTagsResult is Success && interestTagsResult is Success -> {
                originNotificationTags = interestTagsResult.data
                _notificationTags.value = NotificationTagsConfigUiState(
                    eventTags = eventTagsResult.data,
                    interestEventTags = interestTagsResult.data,
                )
            }
        }
        _networkUiState.value = NetworkUiState.NONE
    }

    override fun refresh(): Job = viewModelScope.launch {
        val (eventTagsResult, interestTagsResult) = listOf(
            async { eventTagRepository.getEventTags() },
            async { eventTagRepository.getInterestEventTags(uid) },
        ).awaitAll()

        when {
            eventTagsResult is Unexpected ->
                _networkUiEvent.value = NetworkUiEvent.Unexpected(eventTagsResult.error.toString())

            interestTagsResult is Unexpected ->
                _networkUiEvent.value =
                    NetworkUiEvent.Unexpected(interestTagsResult.error.toString())

            eventTagsResult is Failure || interestTagsResult is Failure -> dispatchFetchFailEvent()
            eventTagsResult is NetworkError || interestTagsResult is NetworkError -> {
                dispatchNetworkErrorEvent()
                return@launch
            }

            eventTagsResult is Success && interestTagsResult is Success -> {
                originNotificationTags = interestTagsResult.data
                _notificationTags.value = NotificationTagsConfigUiState(
                    eventTags = eventTagsResult.data,
                    interestEventTags = interestTagsResult.data,
                )
            }
        }
        _networkUiState.value = NetworkUiState.NONE
    }

    fun saveInterestEventTag(): Job = command(
        command = {
            val interestTags = _notificationTags.value.eventTags
                .filter { it.isChecked }
                .map { it.eventTag }
            eventTagRepository.updateInterestEventTags(interestTags)
        },
        onSuccess = { _uiEvent.value = NotificationTagConfigUiEvent.UpdateComplete },
        onFailure = { _, _ -> _uiEvent.value = NotificationTagConfigUiEvent.UpdateFail },
    )

    fun checkTag(tagId: Long) {
        _notificationTags.value = notificationTags.value.checkTag(tagId)
    }

    fun uncheckTag(tagId: Long) {
        _notificationTags.value = notificationTags.value.uncheckTag(tagId)
    }
}
