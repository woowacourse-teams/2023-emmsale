package com.emmsale.presentation.ui.notificationTagConfig

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.model.EventTag
import com.emmsale.data.repository.interfaces.EventTagRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.common.firebase.analytics.logInterestTags
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.ui.notificationTagConfig.uiState.NotificationTagConfigUiEvent
import com.emmsale.presentation.ui.notificationTagConfig.uiState.NotificationTagConfigUiState
import com.emmsale.presentation.ui.notificationTagConfig.uiState.NotificationTagsConfigUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationTagConfigViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val eventTagRepository: EventTagRepository,
) : ViewModel(), Refreshable {
    private val _notificationTags = NotNullMutableLiveData(NotificationTagsConfigUiState())
    val notificationTags: NotNullLiveData<NotificationTagsConfigUiState> = _notificationTags

    private val _event = MutableLiveData<NotificationTagConfigUiEvent?>(null)
    val event: LiveData<NotificationTagConfigUiEvent?> = _event

    init {
        refresh()
    }

    override fun refresh() {
        _notificationTags.value = notificationTags.value.copy(isLoading = true)

        viewModelScope.launch {
            val memberId = tokenRepository.getToken()?.uid ?: return@launch

            val (eventTags, interestEventTags) = awaitAll(
                getEventTagsAsync(),
                getInterestEventTagsAsync(memberId),
            )

            if (eventTags == null || interestEventTags == null) {
                _notificationTags.value =
                    _notificationTags.value.copy(isLoading = false, isError = true)
                return@launch
            }
            _notificationTags.value = NotificationTagsConfigUiState.from(
                eventTags = eventTags,
                interestEventTags = interestEventTags,
            )
        }
    }

    private suspend fun getEventTagsAsync(): Deferred<List<EventTag>?> = viewModelScope.async {
        when (val result = eventTagRepository.getEventTags()) {
            is Success -> result.data
            is Failure, NetworkError -> null
            is Unexpected -> throw Throwable(result.error)
        }
    }

    private suspend fun getInterestEventTagsAsync(memberId: Long): Deferred<List<EventTag>?> =
        viewModelScope.async {
            when (val result = eventTagRepository.getInterestEventTags(memberId)) {
                is Failure, NetworkError -> null
                is Success -> result.data
                is Unexpected -> throw Throwable(result.error)
            }
        }

    fun saveInterestEventTagIds() {
        viewModelScope.launch {
            val interestEventTags = notificationTags.value.conferenceTags
                .filter(NotificationTagConfigUiState::isChecked)
                .map { EventTag(id = it.id, name = it.tagName) }

            when (val result = eventTagRepository.updateInterestEventTags(interestEventTags)) {
                is Failure, NetworkError -> _event.value = NotificationTagConfigUiEvent.UPDATE_FAIL
                is Success -> {
                    _event.value = NotificationTagConfigUiEvent.UPDATE_SUCCESS
                    logInterestTags(interestEventTags.map(EventTag::name))
                }

                is Unexpected -> throw Throwable(result.error)
            }
        }
    }

    fun addInterestTag(tagId: Long) {
        _notificationTags.value = notificationTags.value.addInterestTagById(tagId)
    }

    fun removeInterestTag(tagId: Long) {
        _notificationTags.value = notificationTags.value.removeInterestTagById(tagId)
    }

    fun resetEvent() {
        _event.value = null
    }
}
