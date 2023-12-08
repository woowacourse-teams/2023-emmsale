package com.emmsale.presentation.ui.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentSettingBinding
import com.emmsale.presentation.base.NetworkFragment
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegate
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegateImpl
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.ui.blockMemberList.BlockedMembersActivity
import com.emmsale.presentation.ui.login.LoginActivity
import com.emmsale.presentation.ui.myCommentList.MyCommentsActivity
import com.emmsale.presentation.ui.myRecruitmentList.MyRecruitmentActivity
import com.emmsale.presentation.ui.notificationConfig.NotificationConfigActivity
import com.emmsale.presentation.ui.setting.uiState.SettingUiEvent
import com.emmsale.presentation.ui.useTerm.UseTermWebViewActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment :
    NetworkFragment<FragmentSettingBinding>(),
    FirebaseAnalyticsDelegate by FirebaseAnalyticsDelegateImpl("setting") {

    override val layoutResId: Int = R.layout.fragment_setting
    override val viewModel: SettingViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        registerScreen(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDataBinding()

        observeUiEvent()
    }

    private fun setupDataBinding() {
        binding.viewModel = viewModel
        binding.onWritingsButtonClick = { MyRecruitmentActivity.startActivity(requireContext()) }
        binding.onWrittenCommentsButtonClick =
            { MyCommentsActivity.startActivity(requireContext()) }
        binding.onNotificationSettingButtonClick =
            { NotificationConfigActivity.startActivity(requireContext()) }
        binding.onBlockMembersButtonClick = { BlockedMembersActivity.startActivity(requireContext()) }
        binding.onUseTermButtonClick = { UseTermWebViewActivity.startActivity(requireContext()) }
        binding.onLogoutButtonClick = ::showLogoutConfirmDialog
        binding.onInquirePageButtonClick = ::navigateToInquirePage
    }

    private fun showLogoutConfirmDialog() {
        WarningDialog(
            context = context ?: return,
            title = getString(R.string.logoutdialog_title),
            message = getString(R.string.logoutdialog_message),
            positiveButtonLabel = getString(R.string.logoutdialog_positive_button_label),
            negativeButtonLabel = getString(R.string.logoutdialog_negative_button_label),
            onPositiveButtonClick = { viewModel.logout() },
        ).show()
    }

    private fun navigateToInquirePage() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(INQUIRE_PAGE_URL))
        startActivity(intent)
    }

    private fun observeUiEvent() {
        viewModel.uiEvent.observe(viewLifecycleOwner, ::handleUiEvent)
    }

    private fun handleUiEvent(uiEvent: SettingUiEvent) {
        when (uiEvent) {
            SettingUiEvent.Logout -> {
                LoginActivity.startActivity(context ?: return)
                activity?.finish()
            }
        }
    }

    companion object {
        const val TAG: String = "TAG_SETTING"
        private const val INQUIRE_PAGE_URL = "https://forms.gle/wqivV4RKHgcmsHWN9"
    }
}
