package com.emmsale.presentation.ui.postList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.model.Post
import com.emmsale.data.repository.interfaces.PostRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.postList.uiState.PostsUiState
import kotlinx.coroutines.launch

class PostListViewModel(
    private val postRepository: PostRepository,
) : ViewModel(), Refreshable {
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
            when (val postsFetchResult = postRepository.getPosts()) {
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
        fun factory(eventId: Long) = ViewModelFactory {
            PostListViewModel(KerdyApplication.repositoryContainer.postRepository)
        }
    }
}
