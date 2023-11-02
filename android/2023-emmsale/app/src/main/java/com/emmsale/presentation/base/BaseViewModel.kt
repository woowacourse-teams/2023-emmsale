package com.emmsale.presentation.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import com.emmsale.data.common.retrofit.callAdapter.Failure
import com.emmsale.data.common.retrofit.callAdapter.NetworkError
import com.emmsale.data.common.retrofit.callAdapter.Success
import com.emmsale.data.common.retrofit.callAdapter.Unexpected
import com.emmsale.presentation.common.livedata.NotNullLiveData
import com.emmsale.presentation.common.livedata.NotNullMutableLiveData
import com.emmsale.presentation.common.livedata.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    private val _screenUiState = NotNullMutableLiveData(ScreenUiState.NONE)
    val screenUiState: NotNullLiveData<ScreenUiState> = _screenUiState

    private val _baseUiEvent = SingleLiveEvent<BaseUiEvent>()
    val baseUiEvent: LiveData<BaseUiEvent> = _baseUiEvent

    protected fun changeToLoadingState() {
        _screenUiState.value = ScreenUiState.LOADING
    }

    private fun changeToNetworkErrorState() {
        _screenUiState.value = ScreenUiState.NETWORK_ERROR
    }

    protected fun onRequestFailByNetworkError() {
        _baseUiEvent.value = BaseUiEvent.RequestFailByNetworkError
    }

    abstract fun refresh(): Job

    protected fun <T : Any> requestToNetwork(
        getResult: suspend () -> ApiResponse<T>,
        onSuccess: ((T) -> Unit)? = null,
        onFailure: ((code: Int, message: String?) -> Unit)? = null,
        onLoading: (suspend () -> Unit)? = null,
        onNetworkError: (() -> Unit)? = null,
    ): Job = viewModelScope.launch {
        val loadingJob = launch { onLoading?.invoke() ?: changeToLoadingState() }
        when (val result = getResult()) {
            is Failure -> onFailure?.invoke(result.code, result.message)
            NetworkError -> {
                onNetworkError?.invoke() ?: changeToNetworkErrorState()
                return@launch
            }

            is Success -> onSuccess?.invoke(result.data)
            is Unexpected -> _baseUiEvent.value = BaseUiEvent.Unexpected(result.error.toString())
        }
        loadingJob.cancel()
        _screenUiState.value = ScreenUiState.NONE
    }

    protected fun <T : Any> commandAndRefresh(
        command: suspend () -> ApiResponse<T>,
        onSuccess: ((T) -> Unit)? = null,
        onFailure: ((code: Int, message: String?) -> Unit)? = null,
        onLoading: (suspend () -> Unit)? = null,
        onNetworkError: (() -> Unit)? = null,
    ): Job = viewModelScope.launch {
        val loadingJob = launch { onLoading?.invoke() ?: changeToLoadingState() }
        when (val result = command()) {
            is Failure -> onFailure?.invoke(result.code, result.message)
            NetworkError -> onNetworkError?.invoke() ?: onRequestFailByNetworkError()
            is Success -> {
                refresh().join()
                onSuccess?.invoke(result.data)
            }

            is Unexpected -> _baseUiEvent.value = BaseUiEvent.Unexpected(result.error.toString())
        }
        loadingJob.cancel()
        _screenUiState.value = ScreenUiState.NONE
    }
}

enum class ScreenUiState {
    NONE, LOADING, NETWORK_ERROR
}

sealed interface BaseUiEvent {
    data class Unexpected(val errorMessage: String) : BaseUiEvent
    object RequestFailByNetworkError : BaseUiEvent
}
