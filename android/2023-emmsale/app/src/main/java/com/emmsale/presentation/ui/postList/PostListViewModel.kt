package com.emmsale.presentation.ui.postList

import androidx.lifecycle.ViewModel
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.ui.postList.uiState.PostsUiState

class PostListViewModel() : ViewModel(), Refreshable {
    private val _posts = NotNullMutableLiveData(PostsUiState())
    val posts: NotNullLiveData<PostsUiState> = _posts

    init {
        refresh()
    }

    override fun refresh() {
    }

    private fun fetchPosts() {
    }
}
