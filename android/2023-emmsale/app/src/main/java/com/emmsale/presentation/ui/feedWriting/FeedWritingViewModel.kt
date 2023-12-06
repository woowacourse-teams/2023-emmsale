package com.emmsale.presentation.ui.feedWriting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.emmsale.data.repository.interfaces.FeedRepository
import com.emmsale.presentation.base.NetworkViewModel
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.livedata.SingleLiveEvent
import com.emmsale.presentation.ui.feedWriting.uiState.FeedWritingUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FeedWritingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val feedRepository: FeedRepository,
) : NetworkViewModel() {
    private val eventId = savedStateHandle[EVENT_ID_KEY] ?: DEFAULT_ID

    val title = MutableLiveData<String>()
    private val titleIsNotBlank: Boolean
        get() = title.value?.isNotBlank() ?: false

    val content = MutableLiveData<String>()
    private val contentIsNotBlank: Boolean
        get() = content.value?.isNotBlank() ?: false

    private val _imageUris = NotNullMutableLiveData(emptyList<String>())
    val imageUris: NotNullLiveData<List<String>> = _imageUris

    private val _canSubmit = NotNullMutableLiveData(true)
    val calSubmit = MediatorLiveData(false).apply {
        addSource(title) { value = canSubmit() }
        addSource(content) { value = canSubmit() }
        addSource(_canSubmit) { value = canSubmit() }
    }

    private fun canSubmit(): Boolean = titleIsNotBlank && contentIsNotBlank && _canSubmit.value

    private val _uiEvent = SingleLiveEvent<FeedWritingUiEvent>()
    val uiEvent: LiveData<FeedWritingUiEvent> = _uiEvent

    fun uploadPost(imageFiles: List<File>): Job = command(
        command = {
            feedRepository.uploadFeed(
                eventId,
                title.value ?: DEFAULT_TITLE,
                content.value ?: DEFAULT_CONTENT,
                imageFiles,
            )
        },
        onSuccess = { _uiEvent.value = FeedWritingUiEvent.PostComplete(it) },
        onFailure = { _, _ -> _uiEvent.value = FeedWritingUiEvent.PostFail },
        onLoading = { changeToLoadingState() },
        onStart = { _canSubmit.value = false },
        onFinish = { _canSubmit.value = true },
    )

    fun setImageUris(imageUris: List<String>) {
        _imageUris.value = imageUris
    }

    fun deleteImageUri(imageUri: String) {
        _imageUris.value = _imageUris.value.filter { it != imageUri }
    }

    fun isChanged(): Boolean =
        titleIsNotBlank || contentIsNotBlank || _imageUris.value.isNotEmpty()

    companion object {
        const val EVENT_ID_KEY = "EVENT_ID_KEY"
        private const val DEFAULT_TITLE = ""
        private const val DEFAULT_CONTENT = ""
        private const val DEFAULT_ID = -1L
    }
}
