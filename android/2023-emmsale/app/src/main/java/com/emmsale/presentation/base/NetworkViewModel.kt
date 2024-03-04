package com.emmsale.presentation.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.network.callAdapter.ApiResponse
import com.emmsale.data.network.callAdapter.Failure
import com.emmsale.data.network.callAdapter.NetworkError
import com.emmsale.data.network.callAdapter.Success
import com.emmsale.data.network.callAdapter.Unexpected
import com.emmsale.presentation.common.NetworkUiEvent
import com.emmsale.presentation.common.NetworkUiState
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.livedata.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class NetworkViewModel : ViewModel() {

    protected val _networkUiState = NotNullMutableLiveData(NetworkUiState.NONE)
    val networkUiState: NotNullLiveData<NetworkUiState> = _networkUiState

    protected val _networkUiEvent = SingleLiveEvent<NetworkUiEvent>()
    val networkUiEvent: LiveData<NetworkUiEvent> = _networkUiEvent

    protected fun changeToLoadingState() {
        _networkUiState.value = NetworkUiState.LOADING
    }

    protected fun changeToNetworkErrorState() {
        _networkUiState.value = NetworkUiState.NETWORK_ERROR
    }

    protected fun dispatchNetworkErrorEvent() {
        _networkUiEvent.value = NetworkUiEvent.RequestFailByNetworkError
    }

    protected fun dispatchFetchFailEvent() {
        _networkUiEvent.value = NetworkUiEvent.FetchFail
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
                if (_networkUiState.value == NetworkUiState.NETWORK_ERROR) {
                    loadingJob.cancel()
                    onFinish()
                    return@launch
                }
            }

            is Unexpected ->
                _networkUiEvent.value = NetworkUiEvent.Unexpected(result.error.toString())
        }
        loadingJob.cancel()
        _networkUiState.value = NetworkUiState.NONE
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
