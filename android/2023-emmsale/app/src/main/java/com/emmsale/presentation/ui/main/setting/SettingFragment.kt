package com.emmsale.presentation.ui.main.setting

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentSettingBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegate
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegateImpl
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.ui.login.LoginActivity
import com.emmsale.presentation.ui.main.setting.block.MemberBlockActivity
import com.emmsale.presentation.ui.main.setting.myComments.MyCommentsActivity
import com.emmsale.presentation.ui.main.setting.myPost.MyPostActivity
import com.emmsale.presentation.ui.main.setting.notification.NotificationConfigActivity
import com.emmsale.presentation.ui.main.setting.openProfileUrl.OpenProfileUrlConfigActivity
import com.emmsale.presentation.ui.main.setting.uiState.MemberUiState

class SettingFragment :
    BaseFragment<FragmentSettingBinding>(),
    FirebaseAnalyticsDelegate by FirebaseAnalyticsDelegateImpl("setting") {
    override val layoutResId: Int = R.layout.fragment_setting

    private val viewModel: SettingViewModel by viewModels {
        SettingViewModel.factory
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        registerScreen(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDataBinding()
        setupUiLogic()
    }

    private fun initDataBinding() {
        binding.viewModel = viewModel
        binding.showWritings = ::showWritings
        binding.showWrittenComments = ::showWrittenComments
        binding.showNotificationSetting = ::navigateToNotificationConfig
        binding.showBlocks = ::showBlocks
        binding.showUseTerm = ::showUseTerm
        binding.logout = ::logout
        binding.showOpenProfileUrlConfig = ::showOpenProfileUrlConfig
    }

    private fun showWritings() {
        MyPostActivity.startActivity(requireContext())
    }

    private fun showWrittenComments() {
        MyCommentsActivity.startActivity(context ?: return)
    }

    private fun navigateToNotificationConfig() {
        NotificationConfigActivity.startActivity(requireContext())
    }

    private fun showBlocks() {
        MemberBlockActivity.startActivity(context ?: return)
    }

    private fun showUseTerm() {
        UseTermWebViewActivity.startActivity(requireContext())
    }

    private fun showOpenProfileUrlConfig() {
        OpenProfileUrlConfigActivity.startActivity(requireContext())
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
            handleMemberDelete(it)
            handleLogout(it)
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
        const val TAG: String = "TAG_SETTING"
    }
}
