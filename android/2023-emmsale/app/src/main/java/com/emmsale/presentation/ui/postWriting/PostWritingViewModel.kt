package com.emmsale.presentation.ui.postWriting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.repository.interfaces.PostRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.Event
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.postWriting.uiState.PostUploadResultUiState
import kotlinx.coroutines.launch

class PostWritingViewModel(
    private val eventId: Long,
    private val postRepository: PostRepository,
) : ViewModel() {

    private val _imageUrls: NotNullMutableLiveData<List<String>> =
        NotNullMutableLiveData(emptyList())
    val imageUrls: NotNullLiveData<List<String>> = _imageUrls

    private val _postUploadResult: MutableLiveData<Event<PostUploadResultUiState>> =
        MutableLiveData()
    val postUploadResult: LiveData<Event<PostUploadResultUiState>> = _postUploadResult

    fun uploadPost(
        title: String,
        content: String,
    ) {
        _postUploadResult.value = Event(PostUploadResultUiState(FetchResult.LOADING))
        viewModelScope.launch {
            when (
                val fetchResult =
                    postRepository.uploadPost(eventId, title, content, imageUrls.value)
            ) {
                is Success ->
                    _postUploadResult.value =
                        Event(PostUploadResultUiState(FetchResult.SUCCESS, fetchResult.data))

                else -> _postUploadResult.value = Event(PostUploadResultUiState(FetchResult.ERROR))
            }
        }
    }

    fun fetchImageUrls(imageUrls: List<String>) {
        _imageUrls.value = imageUrls
    }

    fun deleteImageUrl(imageUrl: String) {
        val newUrls = _imageUrls.value.toMutableList().apply { remove(imageUrl) }
        _imageUrls.value = newUrls
    }

    companion object {
        fun viewModelFactory(eventId: Long) = ViewModelFactory {
            PostWritingViewModel(
                eventId,
                KerdyApplication.repositoryContainer.postRepository,
            )
        }
    }
}
