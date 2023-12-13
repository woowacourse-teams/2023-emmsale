package com.emmsale.presentation.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.emmsale.R
import com.emmsale.presentation.common.NetworkUiEvent
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.views.InfoDialog

abstract class NetworkFragment<V : ViewDataBinding>(
    @LayoutRes layoutResId: Int,
) : BaseFragment<V>(layoutResId) {

    abstract val viewModel: NetworkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeCommentUiEvent()
    }

    private fun observeCommentUiEvent() {
        viewModel.networkUiEvent.observe(this) { handleCommentUiEvent(it) }
    }

    private fun handleCommentUiEvent(event: NetworkUiEvent) {
        when (event) {
            NetworkUiEvent.RequestFailByNetworkError -> binding.root.showSnackBar(getString(R.string.all_network_check_message))
            is NetworkUiEvent.Unexpected -> showUnexpectedErrorOccurredDialog()
            is NetworkUiEvent.FetchFail -> showIllegalAccessDialog()
        }
    }

    private fun showUnexpectedErrorOccurredDialog() {
        InfoDialog(
            context = requireContext(),
            title = getString(R.string.all_fetch_error_dialog_title),
            message = getString(R.string.all_unexpected_error_occurred_dialog_message),
            buttonLabel = getString(R.string.all_okay),
            onButtonClick = { activity?.finish() },
            cancelable = false,
        ).show()
    }

    private fun showIllegalAccessDialog() {
        InfoDialog(
            context = requireContext(),
            title = getString(R.string.all_fetch_error_dialog_title),
            message = getString(R.string.all_fetch_error_dialog_message),
            buttonLabel = getString(R.string.all_okay),
            onButtonClick = { activity?.finish() },
            cancelable = false,
        ).show()
    }
}
