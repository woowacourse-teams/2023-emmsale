package com.emmsale.presentation.base

import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import kotlinx.coroutines.Job

abstract class RefreshableViewModel : NetworkViewModel() {

    abstract fun refresh(): Job

    protected fun <T : Any> fetchData(
        fetchData: suspend () -> ApiResponse<T>,
        onSuccess: suspend (T) -> Unit = {},
        onFailure: suspend (code: Int, message: String?) -> Unit = { _, _ -> dispatchFetchFailEvent() },
        onLoading: suspend () -> Unit = { changeToLoadingState() },
        onNetworkError: suspend () -> Unit = { changeToNetworkErrorState() },
        onStart: suspend () -> Unit = {},
        onFinish: suspend () -> Unit = {},
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
        onSuccess: suspend (T) -> Unit = {},
        onFailure: suspend (code: Int, message: String?) -> Unit = { _, _ -> dispatchFetchFailEvent() },
        onLoading: suspend () -> Unit = {},
        onNetworkError: suspend () -> Unit = { dispatchNetworkErrorEvent() },
        onStart: suspend () -> Unit = {},
        onFinish: suspend () -> Unit = {},
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
        onFailure: suspend (code: Int, message: String?) -> Unit = { _, _ -> },
        onLoading: suspend () -> Unit = { delayLoading() },
        onNetworkError: suspend () -> Unit = { dispatchNetworkErrorEvent() },
        onStart: suspend () -> Unit = {},
        onFinish: suspend () -> Unit = {},
        refresh: suspend () -> Job = { this@RefreshableViewModel.refresh() },
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
