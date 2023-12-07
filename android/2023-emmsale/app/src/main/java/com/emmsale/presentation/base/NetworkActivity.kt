package com.emmsale.presentation.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.emmsale.R
import com.emmsale.presentation.common.CommonUiEvent
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.extension.showToast
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
        viewModel.commonUiEvent.observe(this) { handleCommonUiEvent(it) }
    }

    private fun handleCommonUiEvent(event: CommonUiEvent) {
        when (event) {
            CommonUiEvent.RequestFailByNetworkError -> binding.root.showSnackBar(getString(R.string.all_network_check_message))
            is CommonUiEvent.Unexpected -> showToast(event.errorMessage)
            is CommonUiEvent.FetchFail -> showIllegalAccessDialog()
        }
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
