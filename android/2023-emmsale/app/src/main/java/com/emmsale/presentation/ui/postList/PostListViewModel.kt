package com.emmsale.presentation.ui.postList

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.model.Post
import com.emmsale.data.repository.interfaces.PostRepository
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.ui.postList.uiState.PostsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
) : ViewModel(), Refreshable {
    val eventId: Long = savedStateHandle[EVENT_ID_KEY] ?: DEFAULT_EVENT_ID

    private val _posts = NotNullMutableLiveData(PostsUiState())
    val posts: NotNullLiveData<PostsUiState> = _posts

    init {
        refresh()
    }

    override fun refresh() {
        fetchPosts()
    }

    private fun fetchPosts() {
        _posts.toLoadingState()
        viewModelScope.launch {
            when (val postsFetchResult = postRepository.getPosts(eventId)) {
                is Success -> _posts.toSuccessState(postsFetchResult.data)
                else -> _posts.toErrorState()
            }
        }
    }

    private fun NotNullMutableLiveData<PostsUiState>.toSuccessState(posts: List<Post>) {
        this.value = PostsUiState(FetchResult.SUCCESS, posts)
    }

    private fun NotNullMutableLiveData<PostsUiState>.toErrorState() {
        this.value = this.value.copy(fetchResult = FetchResult.ERROR)
    }

    private fun NotNullMutableLiveData<PostsUiState>.toLoadingState() {
        this.value = this.value.copy(fetchResult = FetchResult.LOADING)
    }

    companion object {
        const val EVENT_ID_KEY = "EVENT_ID_KEY"
        private const val DEFAULT_EVENT_ID = -1L
    }
}
