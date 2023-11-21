package com.emmsale.presentation.ui.postWriting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.repository.interfaces.PostRepository
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.UiEvent
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.postWriting.uiState.PostUploadResultUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PostWritingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
) : ViewModel() {
    private val eventId = savedStateHandle[EVENT_ID_KEY] ?: DEFAULT_ID

    private val _imageUris: NotNullMutableLiveData<List<String>> =
        NotNullMutableLiveData(emptyList())
    val imageUris: NotNullLiveData<List<String>> = _imageUris

    private val _postUploadResult: MutableLiveData<UiEvent<PostUploadResultUiState>> =
        MutableLiveData()
    val postUploadResult: LiveData<UiEvent<PostUploadResultUiState>> = _postUploadResult

    val title = MutableLiveData<String>()
    val content = MutableLiveData<String>()

    fun uploadPost(imageFiles: List<File>) {
        _postUploadResult.value = UiEvent(PostUploadResultUiState(FetchResult.LOADING))
        viewModelScope.launch {
            when (
                val fetchResult =
                    postRepository.uploadPost(
                        eventId,
                        title.value ?: DEFAULT_TITLE,
                        content.value ?: DEFAULT_CONTENT,
                        imageFiles,
                    )
            ) {
                is Success ->
                    _postUploadResult.value =
                        UiEvent(PostUploadResultUiState(FetchResult.SUCCESS, fetchResult.data))

                else ->
                    _postUploadResult.value =
                        UiEvent(PostUploadResultUiState(FetchResult.ERROR))
            }
        }
    }

    fun isTitleValid(): Boolean {
        return (title.value?.length ?: 0) >= MINIMUM_TITLE_LENGTH
    }

    fun isContentValid(): Boolean {
        return (content.value?.length ?: 0) >= MINIMUM_CONTENT_LENGTH
    }

    fun fetchImageUris(imageUrls: List<String>) {
        _imageUris.value = imageUrls
    }

    fun deleteImageUrl(imageUrl: String) {
        val newUrls = _imageUris.value.toMutableList().apply { remove(imageUrl) }
        _imageUris.value = newUrls
    }

    companion object {
        const val EVENT_ID_KEY = "EVENT_ID_KEY"
        private const val DEFAULT_TITLE = ""
        private const val DEFAULT_CONTENT = ""
        private const val DEFAULT_ID = -1L
        private const val MINIMUM_TITLE_LENGTH = 1
        private const val MINIMUM_CONTENT_LENGTH = 8
    }
}
