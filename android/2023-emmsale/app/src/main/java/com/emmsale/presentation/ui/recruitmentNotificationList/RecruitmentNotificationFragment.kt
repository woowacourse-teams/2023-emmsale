package com.emmsale.presentation.ui.recruitmentNotificationList

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentRecruitmentNotificationBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.views.InfoDialog
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.ui.profile.ProfileActivity
import com.emmsale.presentation.ui.recruitmentNotificationList.recyclerView.body.RecruitmentNotificationBodyClickListener
import com.emmsale.presentation.ui.recruitmentNotificationList.recyclerView.header.RecruitmentNotificationHeaderAdapter
import com.emmsale.presentation.ui.recruitmentNotificationList.recyclerView.header.RecruitmentNotificationHeaderClickListener
import com.emmsale.presentation.ui.recruitmentNotificationList.uiState.RecruitmentNotificationUiEvent

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
        setupUiEvent()
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
        WarningDialog(
            context = context ?: return,
            title = getString(R.string.all_report_dialog_title),
            message = getString(R.string.recruitmentnotification_report_dialog_message),
            positiveButtonLabel = getString(R.string.all_report_dialog_positive_button_label),
            negativeButtonLabel = getString(R.string.all_cancel),
            onPositiveButtonClick = { viewModel.reportRecruitmentNotification(notificationId) },
        ).show()
    }

    override fun onOpenChatButtonClick(openChatUrl: String) {
        if (openChatUrl.isEmpty()) {
            showUnregisteredOpenChatUrlErrorMessage()
            return
        }

        navigateToChat(openChatUrl)
    }

    override fun onSenderImageClick(memberId: Long) {
        ProfileActivity.startActivity(context ?: return, memberId)
    }

    private fun showUnregisteredOpenChatUrlErrorMessage() {
        binding.root.showSnackBar(R.string.recruitmentnotification_unregistered_sender_open_chat_url)
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
                !uiState.isLoading ->
                    recruitmentNotificationHeaderAdapter.submitList(uiState.notificationGroups)
            }
        }
    }

    private fun setupUiEvent() {
        viewModel.event.observe(viewLifecycleOwner) {
            handleEvent(it)
        }
    }

    private fun handleEvent(event: RecruitmentNotificationUiEvent?) {
        if (event == null) return
        when (event) {
            RecruitmentNotificationUiEvent.REPORT_FAIL -> binding.root.showSnackBar(getString(R.string.all_report_fail_message))
            RecruitmentNotificationUiEvent.REPORT_SUCCESS -> InfoDialog(
                context = context ?: return,
                title = getString(R.string.all_report_complete_dialog_title),
                message = getString(R.string.all_report_complete_dialog_message),
                buttonLabel = getString(R.string.all_okay),
            ).show()

            RecruitmentNotificationUiEvent.CHANGE_RECRUITMENT_STATUS_FAIL -> binding.root.showSnackBar(
                getString(R.string.notificationbox_recruitment_status_changing_failed),
            )

            RecruitmentNotificationUiEvent.ACCEPT_COMPLETE -> showRecruitmentAcceptedDialog()
            RecruitmentNotificationUiEvent.REJECT_COMPLETE -> binding.root.showSnackBar(getString(R.string.notificationbox_recruitment_rejected_message))
            RecruitmentNotificationUiEvent.ACCEPT_FAIL -> {}
            RecruitmentNotificationUiEvent.REJECT_FAIL -> {}
            RecruitmentNotificationUiEvent.REPORT_DUPLICATE -> InfoDialog(
                context = context ?: return,
                title = getString(R.string.all_report_duplicate_dialog_title),
                message = getString(R.string.all_report_duplicate_message),
                buttonLabel = getString(R.string.all_okay),
            ).show()
        }
        viewModel.removeEvent()
    }

    private fun showRecruitmentAcceptedDialog() {
        RecruitmentAcceptedDialog(requireContext()).show()
    }
}
