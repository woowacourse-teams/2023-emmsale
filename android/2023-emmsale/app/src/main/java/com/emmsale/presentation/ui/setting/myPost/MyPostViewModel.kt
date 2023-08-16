package com.emmsale.presentation.ui.setting.myPost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.myPost.MyPostRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.ViewModelFactory
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.ui.setting.myPost.uiState.MyPostsUiState
import kotlinx.coroutines.launch

class MyPostViewModel(
    private val myPostRepository: MyPostRepository,
) : ViewModel() {
    private val _myPosts: NotNullMutableLiveData<MyPostsUiState> =
        NotNullMutableLiveData(MyPostsUiState())
    val myPosts: NotNullLiveData<MyPostsUiState> = _myPosts

    init {
        fetchMyPosts()
    }

    private fun fetchMyPosts() {
        _myPosts.value = _myPosts.value.copy(isLoading = true)
        viewModelScope.launch {
            when (val response = myPostRepository.getMyPosts()) {
                is ApiSuccess -> _myPosts.value = MyPostsUiState.from(response.data)
                is ApiError, is ApiException ->
                    _myPosts.value =
                        _myPosts.value.copy(isLoading = false, isError = true)
            }
        }
    }

    companion object {
        val factory = ViewModelFactory {
            MyPostViewModel(KerdyApplication.repositoryContainer.myPostRepository)
        }
    }
}
