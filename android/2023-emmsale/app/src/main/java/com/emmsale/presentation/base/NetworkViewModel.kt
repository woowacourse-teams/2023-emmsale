package com.emmsale.presentation.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.common.retrofit.callAdapter.NetworkError
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.common.retrofit.callAdapter.Unexpected
import com.emmsale.presentation.common.CommonUiEvent
import com.emmsale.presentation.common.ScreenUiState
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.livedata.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class NetworkViewModel : ViewModel() {

    protected val _screenUiState = NotNullMutableLiveData(ScreenUiState.NONE)
    val screenUiState: NotNullLiveData<ScreenUiState> = _screenUiState

    protected val _commonUiEvent = SingleLiveEvent<CommonUiEvent>()
    val commonUiEvent: LiveData<CommonUiEvent> = _commonUiEvent

    protected fun changeToLoadingState() {
        _screenUiState.value = ScreenUiState.LOADING
    }

    protected fun changeToNetworkErrorState() {
        _screenUiState.value = ScreenUiState.NETWORK_ERROR
    }

    protected fun dispatchNetworkErrorEvent() {
        _commonUiEvent.value = CommonUiEvent.RequestFailByNetworkError
    }

    protected suspend fun delayLoading(timeMillis: Long = LOADING_DELAY) {
        delay(timeMillis)
        changeToLoadingState()
    }

    protected fun <T : Any> requestNetwork(
        request: suspend () -> ApiResponse<T>,
        onSuccess: suspend (T) -> Unit,
        onFailure: suspend (code: Int, message: String?) -> Unit,
        onLoading: suspend () -> Unit,
        onNetworkError: suspend () -> Unit,
        onStart: suspend () -> Unit = {},
        onFinish: suspend () -> Unit = {},
    ): Job = viewModelScope.launch {
        onStart()
        val loadingJob = launch { onLoading() }
        when (val result = request()) {
            is Success -> onSuccess(result.data)
            is Failure -> onFailure(result.code, result.message)
            NetworkError -> {
                onNetworkError()
                if (_screenUiState.value == ScreenUiState.NETWORK_ERROR) {
                    onFinish()
                    return@launch
                }
            }

            is Unexpected ->
                _commonUiEvent.value = CommonUiEvent.Unexpected(result.error.toString())
        }
        loadingJob.cancel()
        _screenUiState.value = ScreenUiState.NONE
        onFinish()
    }

    protected fun <T : Any> command(
        command: suspend () -> ApiResponse<T>,
        onSuccess: suspend (T) -> Unit = {},
        onFailure: suspend (code: Int, message: String?) -> Unit = { _, _ -> },
        onLoading: suspend () -> Unit = { delayLoading() },
        onNetworkError: suspend () -> Unit = { dispatchNetworkErrorEvent() },
        onStart: suspend () -> Unit = {},
        onFinish: suspend () -> Unit = {},
    ): Job = requestNetwork(
        request = { command() },
        onSuccess = { onSuccess(it) },
        onFailure = { code, message -> onFailure(code, message) },
        onLoading = { onLoading() },
        onNetworkError = { onNetworkError() },
        onStart = { onStart() },
        onFinish = { onFinish() },
    )

    companion object {
        private const val LOADING_DELAY: Long = 1000
    }
}
