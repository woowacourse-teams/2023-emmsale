package com.emmsale.presentation.base

import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import kotlinx.coroutines.Job

abstract class RefreshableViewModel : NetworkViewModel() {

    abstract fun refresh(): Job

    protected fun <T : Any> fetchData(
        fetchData: suspend () -> ApiResponse<T>,
        onSuccess: (T) -> Unit = {},
        onFailure: (code: Int, message: String?) -> Unit = { _, _ -> },
        onLoading: suspend () -> Unit = { changeToLoadingState() },
        onNetworkError: () -> Unit = ::changeToNetworkErrorState,
        onStart: () -> Unit = {},
        onFinish: () -> Unit = {},
    ): Job = requestNetwork(
        request = { fetchData() },
        onSuccess = { onSuccess(it) },
        onFailure = { code, message -> onFailure(code, message) },
        onLoading = { onLoading() },
        onNetworkError = { onNetworkError() },
        onStart = { onStart() },
        onFinish = { onFinish() },
    )

    protected fun <T : Any> refreshData(
        refresh: suspend () -> ApiResponse<T>,
        onSuccess: (T) -> Unit = {},
        onFailure: (code: Int, message: String?) -> Unit = { _, _ -> },
        onLoading: suspend () -> Unit = {},
        onNetworkError: () -> Unit = {},
        onStart: () -> Unit = {},
        onFinish: () -> Unit = {},
    ): Job = requestNetwork(
        request = { refresh() },
        onSuccess = { onSuccess(it) },
        onFailure = { code, message -> onFailure(code, message) },
        onLoading = { onLoading() },
        onNetworkError = { onNetworkError() },
        onStart = { onStart() },
        onFinish = { onFinish() },
    )

    protected fun <T : Any> commandAndRefresh(
        command: suspend () -> ApiResponse<T>,
        onSuccess: suspend (T) -> Unit = {},
        onFailure: (code: Int, message: String?) -> Unit = { _, _ -> },
        onLoading: suspend () -> Unit = { delayLoading() },
        onNetworkError: () -> Unit = ::dispatchNetworkErrorEvent,
        onStart: () -> Unit = {},
        onFinish: () -> Unit = {},
        refresh: () -> Job = { this@RefreshableViewModel.refresh() },
    ): Job = requestNetwork(
        request = { command() },
        onSuccess = {
            refresh().join()
            onSuccess(it)
        },
        onFailure = { code, message -> onFailure(code, message) },
        onLoading = { onLoading() },
        onNetworkError = { onNetworkError() },
        onStart = { onStart() },
        onFinish = { onFinish() },
    )
}
