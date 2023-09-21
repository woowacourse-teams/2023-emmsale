package com.emmsale.presentation.ui.myCommentList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.callAdapter.Failure
import com.emmsale.data.common.callAdapter.NetworkError
import com.emmsale.data.common.callAdapter.Success
import com.emmsale.data.common.callAdapter.Unexpected
import com.emmsale.data.repository.interfaces.CommentRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.Refreshable
import com.emmsale.presentation.ui.myCommentList.uiState.MyCommentsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyCommentsViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val commentRepository: CommentRepository,
) : ViewModel(), Refreshable {

    private val _isLogin = NotNullMutableLiveData(true)
    val isLogin: NotNullLiveData<Boolean> = _isLogin

    private val _comments = NotNullMutableLiveData(MyCommentsUiState.FIRST_LOADING)
    val comments: NotNullLiveData<MyCommentsUiState> = _comments

    init {
        refresh()
    }

    override fun refresh() {
        viewModelScope.launch {
            val token = tokenRepository.getToken()
            if (token == null) {
                _isLogin.value = false
                return@launch
            }

            when (val result = commentRepository.getCommentsByMemberId(token.uid)) {
                is Failure, NetworkError -> _comments.value = _comments.value.changeToErrorState()
                is Success ->
                    _comments.value = _comments.value.setCommentsState(result.data, token.uid)

                is Unexpected -> throw Throwable(result.error)
            }
        }
    }
}
