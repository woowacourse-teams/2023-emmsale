package com.emmsale.presentation.ui.main.setting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentSettingBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.ui.login.LoginActivity
import com.emmsale.presentation.ui.main.setting.block.MemberBlockActivity
import com.emmsale.presentation.ui.main.setting.myComments.MyCommentsActivity
import com.emmsale.presentation.ui.main.setting.myPost.MyPostActivity
import com.emmsale.presentation.ui.main.setting.notification.NotificationConfigActivity
import com.emmsale.presentation.ui.main.setting.uiState.MemberUiState

class SettingFragment : BaseFragment<FragmentSettingBinding>() {
    override val layoutResId: Int = R.layout.fragment_setting

    private val viewModel: SettingViewModel by viewModels {
        SettingViewModel.factory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDataBinding()
        setupUiLogic()

        viewModel.fetchMember()
    }

    private fun initDataBinding() {
        binding.viewModel = viewModel
        binding.showWritings = ::showWritings
        binding.showWrittenComments = ::showWrittenComments
        binding.showNotificationSetting = ::navigateToNotificationConfig
        binding.showBlocks = ::showBlocks
        binding.showUseTerm = ::showUseTerm
        binding.deleteMember = ::deleteMember
        binding.logout = ::logout
    }

    private fun showWritings() {
        MyPostActivity.startActivity(requireContext())
    }

    private fun showWrittenComments() {
        MyCommentsActivity.startActivity(context ?: return)
    }

    private fun navigateToNotificationConfig() {
        startActivity(NotificationConfigActivity.getIntent(requireContext()))
    }

    private fun showBlocks() {
        MemberBlockActivity.startActivity(context ?: return)
    }

    private fun showUseTerm() {
        UseTermWebViewActivity.startActivity(requireContext())
    }

    private fun deleteMember() {
        WarningDialog(
            context = context ?: return,
            title = getString(R.string.memberdeletedialog_title),
            message = getString(R.string.memberdeletedialog_message),
            positiveButtonLabel = getString(R.string.memberdeletedialog_positive_button_label),
            negativeButtonLabel = getString(R.string.memberdeletedialog_negative_button_label),
            onPositiveButtonClick = { viewModel.deleteMember() },
        ).show()
    }

    private fun logout() {
        WarningDialog(
            context = context ?: return,
            title = getString(R.string.logoutdialog_title),
            message = getString(R.string.logoutdialog_message),
            positiveButtonLabel = getString(R.string.logoutdialog_positive_button_label),
            negativeButtonLabel = getString(R.string.logoutdialog_negative_button_label),
            onPositiveButtonClick = { viewModel.logout() },
        ).show()
    }

    private fun setupUiLogic() {
        setupLoginUiLogic()
        setupMemberUiLogic()
    }

    private fun setupLoginUiLogic() {
        viewModel.isLogin.observe(viewLifecycleOwner) {
            handleNotLogin(it)
        }
    }

    private fun handleNotLogin(isLogin: Boolean) {
        if (!isLogin) {
            LoginActivity.startActivity(requireContext())
            activity?.finish()
        }
    }

    private fun setupMemberUiLogic() {
        viewModel.member.observe(viewLifecycleOwner) {
            handleMemberFetchingError(it)
            handleMemberDeleteError(it)
            handleMemberDelete(it)
            handleLogout(it)
        }
    }

    private fun handleMemberFetchingError(member: MemberUiState) {
        if (member.isFetchingError) {
            context?.showToast(getString(R.string.setting_member_fetching_error_message))
        }
    }

    private fun handleMemberDeleteError(member: MemberUiState) {
        if (member.isDeleteError) {
            context?.showToast(getString(R.string.setting_member_delete_error_message))
        }
    }

    private fun handleMemberDelete(member: MemberUiState) {
        if (member.isDeleted) {
            LoginActivity.startActivity(context ?: return)
            activity?.finish()
        }
    }

    private fun handleLogout(member: MemberUiState) {
        if (member.isLogout) {
            LoginActivity.startActivity(context ?: return)
            activity?.finish()
        }
    }

    companion object {
        val TAG: String = SettingFragment::class.java.simpleName
    }
}
