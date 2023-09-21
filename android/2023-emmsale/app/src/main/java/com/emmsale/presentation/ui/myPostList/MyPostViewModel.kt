package com.emmsale.presentation.ui.myPostList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.repository.interfaces.MyPostRepository
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.ui.myPostList.uiState.MyPostsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPostViewModel @Inject constructor(
    private val myPostRepository: MyPostRepository,
) : ViewModel(), Refreshable {
    private val _myPosts: NotNullMutableLiveData<MyPostsUiState> =
        NotNullMutableLiveData(MyPostsUiState())
    val myPosts: NotNullLiveData<MyPostsUiState> = _myPosts

    init {
        refresh()
    }

    override fun refresh() {
        _myPosts.value = _myPosts.value.copy(isLoading = true)
        viewModelScope.launch {
            when (val result = myPostRepository.getMyPosts()) {
                is Failure, NetworkError ->
                    _myPosts.value =
                        _myPosts.value.copy(isLoading = false, isError = true)

                is Success -> _myPosts.value = MyPostsUiState.from(result.data)
                is Unexpected -> throw Throwable(result.error)
            }
        }
    }
}
