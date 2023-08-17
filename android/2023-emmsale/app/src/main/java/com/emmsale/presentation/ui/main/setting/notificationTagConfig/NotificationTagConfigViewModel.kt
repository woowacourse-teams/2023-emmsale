package com.emmsale.presentation.ui.main.setting.notificationTagConfig

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
import com.emmsale.presentation.common.firebase.analytics.logInterestTags
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.main.setting.notificationTagConfig.uistate.NotificationTagConfigUiState
import com.emmsale.presentation.ui.main.setting.notificationTagConfig.uistate.NotificationTagsConfigUiState
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class NotificationTagConfigViewModel(
    private val tokenRepository: TokenRepository,
    private val eventTagRepository: EventTagRepository,
) : ViewModel() {
    private val _notificationTags = NotNullMutableLiveData(NotificationTagsConfigUiState())
    val notificationTags: NotNullLiveData<NotificationTagsConfigUiState> = _notificationTags

    init {
        fetchNotificationTags()
    }

    private fun fetchNotificationTags() {
        _notificationTags.value = notificationTags.value.copy(isLoading = true)

        viewModelScope.launch {
            val memberId = tokenRepository.getToken()?.uid ?: return@launch

            val (eventTags, interestEventTagIds) = awaitAll(
                getEventTagsAsync(),
                getInterestEventTagsAsync(memberId),
            )

            _notificationTags.value = NotificationTagsConfigUiState.from(
                eventTags = eventTags,
                interestEventTags = interestEventTagIds,
            )
        }
    }

    private suspend fun getEventTagsAsync(): Deferred<List<EventTag>> = viewModelScope.async {
        // TODO(추후 컨퍼런스와 대회 태그가 분리될 예정이지만 일단 동일하기 때문에 임시로 컨퍼런스 태그만 가져옴)
        when (val result = eventTagRepository.getEventTags(EventCategory.CONFERENCE)) {
            is ApiSuccess -> result.data
            is ApiError, is ApiException -> {
                changeToTagFetchingErrorState()
                emptyList()
            }
        }
    }

    private fun changeToTagFetchingErrorState() {
        _notificationTags.value = notificationTags.value.copy(
            isLoading = false,
            isTagFetchingError = true,
        )
    }

    private suspend fun getInterestEventTagsAsync(memberId: Long): Deferred<List<EventTag>> =
        viewModelScope.async {
            when (val result = eventTagRepository.getInterestEventTags(memberId)) {
                is ApiSuccess -> result.data
                is ApiError, is ApiException -> {
                    changeToInterestingTagFetchingErrorState()
                    emptyList()
                }
            }
        }

    private fun changeToInterestingTagFetchingErrorState() {
        _notificationTags.value = notificationTags.value.copy(
            isLoading = false,
            isInterestTagFetchingError = true,
        )
    }

    fun saveInterestEventTagIds() {
        viewModelScope.launch {
            _notificationTags.value = notificationTags.value.copy(isLoading = true)
            val interestEventTags = notificationTags.value.conferenceTags
                .filter(NotificationTagConfigUiState::isChecked)
                .map { EventTag(id = it.id, name = it.tagName) }

            when (eventTagRepository.updateInterestEventTags(interestEventTags)) {
                is ApiSuccess -> {
                    _notificationTags.value = notificationTags.value.copy(
                        isInterestTagsUpdateSuccess = true,
                    )
                    logInterestTags(interestEventTags.map(EventTag::name))
                }

                is ApiError, is ApiException -> changeToInterestTagsUpdatingError()
            }
        }
    }

    private fun changeToInterestTagsUpdatingError() {
        _notificationTags.value = notificationTags.value.copy(
            isLoading = false,
            isInterestTagsUpdatingError = true,
        )
    }

    fun addInterestTag(tagId: Long) {
        _notificationTags.value = notificationTags.value.addInterestTagById(tagId)
    }

    fun removeInterestTag(tagId: Long) {
        _notificationTags.value = notificationTags.value.removeInterestTagById(tagId)
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
