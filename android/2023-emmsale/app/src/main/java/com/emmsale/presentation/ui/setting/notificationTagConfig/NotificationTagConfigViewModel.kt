package com.emmsale.presentation.ui.setting.notificationTagConfig

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.event.EventCategory
import com.emmsale.data.eventTag.EventTag
import com.emmsale.data.eventTag.EventTagRepository
import com.emmsale.data.token.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.setting.notificationTagConfig.uistate.NotificationTagsUiState
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class NotificationTagConfigViewModel(
    private val tokenRepository: TokenRepository,
    private val eventTagRepository: EventTagRepository,
) : ViewModel() {

    private val _notificationTags = NotNullMutableLiveData(NotificationTagsUiState())
    val notificationTags: NotNullLiveData<NotificationTagsUiState> = _notificationTags

    init {
        fetchNotificationTags()
    }

    private fun fetchNotificationTags() {
        _notificationTags.value = _notificationTags.value.copy(isLoading = true)

        viewModelScope.launch {
            val memberId = tokenRepository.getToken()?.uid ?: return@launch

            val (eventTags, interestEventTagIds) = awaitAll(
                getEventTagsAsync(),
                getInterestEventTagIdsAsync(memberId),
            )

            _notificationTags.value = NotificationTagsUiState.from(
                eventTags = eventTags.filterIsInstance(EventTag::class.java),
                interestTagIds = interestEventTagIds.filterIsInstance(Long::class.java),
            )
        }
    }

    private suspend fun getEventTagsAsync(): Deferred<List<EventTag>> = viewModelScope.async {
        // TODO(추후 컨퍼런스와 대회 태그가 분리될 예정이지만 일단 동일하기 때문에 임시로 컨퍼런스 태그만 가져옴)
        when (val result = eventTagRepository.getEventTags(EventCategory.CONFERENCE)) {
            is ApiSuccess -> result.data
            is ApiError, is ApiException -> {
                NotificationTagsUiState.TAG_FETCHING_ERROR
                emptyList()
            }
        }
    }

    private suspend fun getInterestEventTagIdsAsync(memberId: Long): Deferred<List<Long>> =
        viewModelScope.async {
            when (val result = eventTagRepository.getInterestEventTags(memberId)) {
                is ApiSuccess -> result.data.map(EventTag::id)
                is ApiError, is ApiException -> {
                    NotificationTagsUiState.INTEREST_TAG_FETCHING_ERROR
                    emptyList()
                }
            }
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
