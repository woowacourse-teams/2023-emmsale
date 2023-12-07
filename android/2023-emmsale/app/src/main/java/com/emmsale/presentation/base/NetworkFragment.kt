package com.emmsale.presentation.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.emmsale.R
import com.emmsale.presentation.common.CommonUiEvent
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.views.InfoDialog

abstract class NetworkFragment<V : ViewDataBinding> : BaseFragment<V>() {

    abstract val viewModel: NetworkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeCommentUiEvent()
    }

    private fun observeCommentUiEvent() {
        viewModel.commonUiEvent.observe(this) { handleCommentUiEvent(it) }
    }

    private fun handleCommentUiEvent(event: CommonUiEvent) {
        when (event) {
            CommonUiEvent.RequestFailByNetworkError -> binding.root.showSnackBar(getString(R.string.all_network_check_message))
            is CommonUiEvent.Unexpected -> showToast(event.errorMessage)
            is CommonUiEvent.FetchFail -> showIllegalAccessDialog()
        }
    }

    private fun showIllegalAccessDialog() {
        InfoDialog(
            context = requireContext(),
            title = "오류",
            message = "잘못된 접근입니다.",
            buttonLabel = "확인",
            onButtonClick = { activity?.finish() },
            cancelable = false,
        ).show()
    }
}
