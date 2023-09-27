package com.emmsale.presentation.ui.myPostList.myRecruitmentPostList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.repository.interfaces.MyPostRepository
import com.emmsale.presentation.common.FetchResult.ERROR
import com.emmsale.presentation.common.FetchResult.LOADING
import com.emmsale.presentation.common.FetchResult.SUCCESS
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.ui.myPostList.myRecruitmentPostList.uiState.MyRecruitmentPostsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyRecruitmentPostListViewModel @Inject constructor(
    private val myPostRepository: MyPostRepository,
) : ViewModel(), Refreshable {
    private val _myPosts: NotNullMutableLiveData<MyRecruitmentPostsUiState> =
        NotNullMutableLiveData(MyRecruitmentPostsUiState())
    val myPosts: NotNullLiveData<MyRecruitmentPostsUiState> = _myPosts

    init {
        refresh()
    }

    override fun refresh() {
        _myPosts.value = _myPosts.value.copy(fetchResult = LOADING)
        viewModelScope.launch {
            when (val result = myPostRepository.getMyPosts()) {
                is Success ->
                    _myPosts.value =
                        _myPosts.value.copy(fetchResult = SUCCESS, successData = result.data)

                is Failure, NetworkError ->
                    _myPosts.value =
                        _myPosts.value.copy(fetchResult = ERROR)

                is Unexpected -> throw Throwable(result.error)
            }
        }
    }
}
