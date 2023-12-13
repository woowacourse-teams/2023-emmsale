package com.emmsale.presentation.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.emmsale.R
import com.emmsale.presentation.common.NetworkUiEvent
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.views.InfoDialog

abstract class NetworkActivity<V : ViewDataBinding>(
    @LayoutRes layoutResId: Int,
) : BaseActivity<V>(layoutResId) {

    protected abstract val viewModel: NetworkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeCommonUiEvent()
    }

    private fun observeCommonUiEvent() {
        viewModel.networkUiEvent.observe(this) { handleCommonUiEvent(it) }
    }

    private fun handleCommonUiEvent(event: NetworkUiEvent) {
        when (event) {
            NetworkUiEvent.RequestFailByNetworkError -> binding.root.showSnackBar(getString(R.string.all_network_check_message))
            is NetworkUiEvent.Unexpected -> showUnexpectedErrorOccurredDialog()
            is NetworkUiEvent.FetchFail -> showIllegalAccessDialog()
        }
    }

    private fun showUnexpectedErrorOccurredDialog() {
        InfoDialog(
            context = this,
            title = getString(R.string.all_fetch_error_dialog_title),
            message = getString(R.string.all_unexpected_error_occurred_dialog_message),
            buttonLabel = getString(R.string.all_okay),
            onButtonClick = { finish() },
            cancelable = false,
        ).show()
    }

    private fun showIllegalAccessDialog() {
        InfoDialog(
            context = this,
            title = getString(R.string.all_fetch_error_dialog_title),
            message = getString(R.string.all_fetch_error_dialog_message),
            buttonLabel = getString(R.string.all_okay),
            onButtonClick = { finish() },
            cancelable = false,
        ).show()
    }
}
