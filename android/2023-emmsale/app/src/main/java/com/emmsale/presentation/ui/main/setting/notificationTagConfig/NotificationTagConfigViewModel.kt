package com.emmsale.presentation.ui.main.setting.notificationTagConfig

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.eventTag.EventTag
import com.emmsale.data.eventTag.EventTagRepository
import com.emmsale.data.token.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.firebase.analytics.logInterestTags
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.main.setting.notificationTagConfig.uistate.NotificationTagConfigUiEvent
import com.emmsale.presentation.ui.main.setting.notificationTagConfig.uistate.NotificationTagConfigUiState
import com.emmsale.presentation.ui.main.setting.notificationTagConfig.uistate.NotificationTagsConfigUiState
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class NotificationTagConfigViewModel(
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

            val (eventTags, interestEventTagIds) = awaitAll(
                getEventTagsAsync(),
                getInterestEventTagsAsync(memberId),
            )

            if (eventTags == null || interestEventTagIds == null) {
                _notificationTags.value =
                    _notificationTags.value.copy(isLoading = false, isError = true)
                return@launch
            }
            _notificationTags.value = NotificationTagsConfigUiState.from(
                eventTags = eventTags,
                interestEventTags = interestEventTagIds,
            )
        }
    }

    private suspend fun getEventTagsAsync(): Deferred<List<EventTag>?> = viewModelScope.async {
        when (val result = eventTagRepository.getEventTags()) {
            is Success -> result.data
            is Unexpected, is Failure, NetworkError -> null
        }
    }

    private suspend fun getInterestEventTagsAsync(memberId: Long): Deferred<List<EventTag>?> =
        viewModelScope.async {
            when (val result = eventTagRepository.getInterestEventTags(memberId)) {
                is ApiSuccess -> result.data
                is ApiError, is ApiException -> null
            }
        }

    fun saveInterestEventTagIds() {
        viewModelScope.launch {
            val interestEventTags = notificationTags.value.conferenceTags
                .filter(NotificationTagConfigUiState::isChecked)
                .map { EventTag(id = it.id, name = it.tagName) }

            when (eventTagRepository.updateInterestEventTags(interestEventTags)) {
                is ApiSuccess -> {
                    _event.value = NotificationTagConfigUiEvent.UPDATE_SUCCESS
                    logInterestTags(interestEventTags.map(EventTag::name))
                }

                is ApiError, is ApiException ->
                    _event.value = NotificationTagConfigUiEvent.UPDATE_FAIL
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

    companion object {
        val factory = ViewModelFactory {
            NotificationTagConfigViewModel(
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
                eventTagRepository = KerdyApplication.repositoryContainer.eventTagRepository,
            )
        }
    }
}
