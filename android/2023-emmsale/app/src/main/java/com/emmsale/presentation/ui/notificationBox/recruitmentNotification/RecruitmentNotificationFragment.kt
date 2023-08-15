package com.emmsale.presentation.ui.notificationBox.recruitmentNotification

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentRecruitmentNotificationBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.extension.showSnackbar
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.ui.notificationBox.recruitmentNotification.dialog.RecruitmentAcceptedDialog
import com.emmsale.presentation.ui.notificationBox.recruitmentNotification.dialog.RecruitmentRejectConfirmDialog
import com.emmsale.presentation.ui.notificationBox.recruitmentNotification.recyclerview.body.RecruitmentNotificationBodyClickListener
import com.emmsale.presentation.ui.notificationBox.recruitmentNotification.recyclerview.header.RecruitmentNotificationHeaderAdapter
import com.emmsale.presentation.ui.notificationBox.recruitmentNotification.recyclerview.header.RecruitmentNotificationHeaderClickListener

class RecruitmentNotificationFragment :
    BaseFragment<FragmentRecruitmentNotificationBinding>(),
    RecruitmentNotificationHeaderClickListener,
    RecruitmentNotificationBodyClickListener {

    override val layoutResId: Int = R.layout.fragment_recruitment_notification
    private val viewModel: RecruitmentNotificationViewModel by viewModels { RecruitmentNotificationViewModel.factory }
    private val recruitmentNotificationHeaderAdapter: RecruitmentNotificationHeaderAdapter by lazy {
        RecruitmentNotificationHeaderAdapter(this, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setupNotifications()
        setupRecruitment()
    }

    private fun initView() {
        binding.viewModel = viewModel
        initNotificationBoxRecyclerView()
    }

    private fun initNotificationBoxRecyclerView() {
        binding.rvRecruitmentNotification.adapter = recruitmentNotificationHeaderAdapter
        binding.rvRecruitmentNotification.setHasFixedSize(true)
    }

    override fun onAcceptButtonClick(notificationId: Long) {
        viewModel.acceptRecruit(notificationId)
    }

    override fun onRejectButtonClick(notificationId: Long) {
        showRecruitmentRejectedConfirmDialog(notificationId)
    }

    override fun onMoreButtonClick(notificationId: Long) {
        viewModel.reportRecruitmentNotification(notificationId)
    }

    override fun onOpenChatButtonClick(openChatUrl: String) {
        if (openChatUrl.isEmpty()) return

        navigateToChat(openChatUrl)
    }

    private fun navigateToChat(chatUrl: String) {
        val profileIntent = Intent(Intent.ACTION_VIEW, Uri.parse(chatUrl))
        startActivity(profileIntent)
    }

    private fun showRecruitmentRejectedConfirmDialog(notificationId: Long) {
        RecruitmentRejectConfirmDialog(
            context = requireContext(),
            object : RecruitmentRejectConfirmDialog.RecruitmentRejectConfirmClickListener {
                override fun onClickOkay(dialog: RecruitmentRejectConfirmDialog) {
                    viewModel.rejectRecruit(notificationId)
                    dialog.dismiss()
                }

                override fun onClickCancel(dialog: RecruitmentRejectConfirmDialog) {
                    dialog.dismiss()
                }
            },
        ).show()
    }

    override fun onNotificationHeaderClick(eventId: Long) {
        viewModel.toggleExpand(eventId)
        viewModel.updateNotificationsToReadStatusBy(eventId)
    }

    private fun setupNotifications() {
        viewModel.notifications.observe(viewLifecycleOwner) { uiState ->
            when {
                uiState.isLoadingNotificationsFailed ->
                    requireContext().showToast(getString(R.string.all_data_loading_failed_message))

                !uiState.isLoading ->
                    recruitmentNotificationHeaderAdapter.submitList(uiState.notificationGroups)
            }
        }
    }

    private fun setupRecruitment() {
        viewModel.recruitmentUiState.observe(viewLifecycleOwner) { uiState ->
            when {
                uiState.isChangingRecruitmentStatusFailed -> requireContext().showToast(getString(R.string.notificationbox_recruitment_status_changing_failed))
                uiState.isAccepted -> showRecruitmentAcceptedDialog()
                uiState.isRejected -> binding.root.showSnackbar(getString(R.string.notificationbox_recruitment_rejected_message))
            }
        }
    }

    private fun showRecruitmentAcceptedDialog() {
        RecruitmentAcceptedDialog(requireContext()).show()
    }
}
