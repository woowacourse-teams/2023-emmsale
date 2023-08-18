package com.emmsale.presentation.ui.main.setting.myComments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.comment.CommentRepository
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.token.TokenRepository
import com.emmsale.presentation.KerdyApplication
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.viewModel.RefreshableViewModel
import com.emmsale.presentation.common.viewModel.ViewModelFactory
import com.emmsale.presentation.ui.main.setting.myComments.uiState.MyCommentsUiState
import kotlinx.coroutines.launch

class MyCommentsViewModel(
    private val tokenRepository: TokenRepository,
    private val commentRepository: CommentRepository,
) : ViewModel(), RefreshableViewModel {

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
                is ApiError, is ApiException ->
                    _comments.value = _comments.value.changeToErrorState()

                is ApiSuccess ->
                    _comments.value = _comments.value.setCommentsState(result.data, token.uid)
            }
        }
    }

    companion object {
        val factory = ViewModelFactory {
            MyCommentsViewModel(
                tokenRepository = KerdyApplication.repositoryContainer.tokenRepository,
                commentRepository = KerdyApplication.repositoryContainer.commentRepository,
            )
        }
    }
}
