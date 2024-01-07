package com.emmsale.presentation.ui.recruitmentDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.emmsale.R
import com.emmsale.databinding.ActivityRecruitmentDetailBinding
import com.emmsale.presentation.base.NetworkActivity
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.views.InfoDialog
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.BottomMenuDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.MenuItemType
import com.emmsale.presentation.ui.messageList.MessageListActivity
import com.emmsale.presentation.ui.profile.ProfileActivity
import com.emmsale.presentation.ui.recruitmentDetail.RecruitmentDetailViewModel.Companion.EVENT_ID_KEY
import com.emmsale.presentation.ui.recruitmentDetail.RecruitmentDetailViewModel.Companion.RECRUITMENT_ID_KEY
import com.emmsale.presentation.ui.recruitmentDetail.uiState.RecruitmentPostDetailUiEvent
import com.emmsale.presentation.ui.recruitmentWriting.RecruitmentWritingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecruitmentDetailActivity :
    NetworkActivity<ActivityRecruitmentDetailBinding>(R.layout.activity_recruitment_detail) {

    override val viewModel: RecruitmentDetailViewModel by viewModels()

    private val postEditorDialog: BottomMenuDialog by lazy {
        BottomMenuDialog(this).apply {
            addMenuItemBelow(
                title = getString(R.string.recruitmentpostdetail_dialog_editing_text),
                menuItemType = MenuItemType.NORMAL,
                onClick = { navigateToEditPage() },
            )

            addMenuItemBelow(
                title = getString(R.string.recruitmentpostdetail_dialog_deletion_text),
                onClick = { showDeleteDialog() },
            )
        }
    }

    private val sendMessageDialog: SendMessageDialog by lazy { SendMessageDialog() }

    private val fetchByResultActivityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result == null || result.resultCode != RESULT_OK) return@registerForActivityResult
        viewModel.refresh()
    }

    private fun navigateToEditPage() {
        val intent = RecruitmentWritingActivity.getEditModeIntent(
            context = this,
            eventId = viewModel.eventId,
            recruitmentId = viewModel.recruitmentId,
            recruitmentContent = viewModel.recruitment.value.recruitment.content,
        )
        fetchByResultActivityLauncher.launch(intent)
    }

    private fun showDeleteDialog() {
        WarningDialog(
            context = this,
            title = getString(R.string.recruitmentpostdetail_delete_dialog_title),
            message = getString(R.string.recruitmentpostdetail_delete_dialog_message),
            positiveButtonLabel = getString(R.string.all_delete_button_label),
            negativeButtonLabel = getString(R.string.all_cancel),
            onPositiveButtonClick = { viewModel.deleteRecruitment() },
        ).show()
    }

    private val postReportDialog: BottomMenuDialog by lazy {
        BottomMenuDialog(this).apply {
            addMenuItemBelow(
                title = getString(R.string.recruitmentpostdetail_dialog_report_text),
                menuItemType = MenuItemType.IMPORTANT,
                onClick = { showReportDialog() },
            )
        }
    }

    private fun showReportDialog() {
        WarningDialog(
            context = this,
            title = getString(R.string.all_report_dialog_title),
            message = getString(R.string.recruitmentpostdetail_report_dialog_message),
            positiveButtonLabel = getString(R.string.all_report_dialog_positive_button_label),
            negativeButtonLabel = getString(R.string.all_cancel),
            onPositiveButtonClick = { viewModel.reportRecruitment() },
        ).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupDataBinding()
        setupBackPressedDispatcher()
        setupToolbar()

        observeUiEvent()
    }

    private fun setupDataBinding() {
        binding.vm = viewModel
        binding.onRequestRecruitmentButtonClick =
            { sendMessageDialog.show(supportFragmentManager, SendMessageDialog.TAG) }
        binding.onProfileImageClick = { memberId -> ProfileActivity.startActivity(this, memberId) }
    }

    private fun setupBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(this) {
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun setupToolbar() {
        binding.tbToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.tbToolbar.setOnMenuItemClickListener {
            if (viewModel.recruitment.value.isMyPost) {
                postEditorDialog.show()
            } else {
                postReportDialog.show()
            }
            true
        }
    }

    private fun observeUiEvent() {
        viewModel.uiEvent.observe(this, ::handleUiEvent)
    }

    private fun handleUiEvent(uiEvent: RecruitmentPostDetailUiEvent) {
        when (uiEvent) {
            is RecruitmentPostDetailUiEvent.MessageSendComplete -> {
                MessageListActivity.startActivity(
                    this,
                    uiEvent.roomId,
                    uiEvent.otherId,
                    true,
                )
                sendMessageDialog.clearText()
                sendMessageDialog.dismiss()
            }

            RecruitmentPostDetailUiEvent.MessageSendFail -> binding.root.showSnackBar(R.string.sendmessagedialog_message_send_fail_message)
            RecruitmentPostDetailUiEvent.PostDeleteComplete -> onBackPressedDispatcher.onBackPressed()
            RecruitmentPostDetailUiEvent.PostDeleteFail -> binding.root.showSnackBar(getString(R.string.recruitmentpostdetail_deletion_fail_message))
            RecruitmentPostDetailUiEvent.PostFetchFail -> binding.root.showSnackBar(getString(R.string.recruitmentpostdetail_fail_request_message))
            RecruitmentPostDetailUiEvent.ReportComplete -> InfoDialog(
                context = this,
                title = getString(R.string.all_report_complete_dialog_title),
                message = getString(R.string.all_report_complete_dialog_message),
                buttonLabel = getString(R.string.all_okay),
            ).show()

            RecruitmentPostDetailUiEvent.ReportDuplicate -> InfoDialog(
                context = this,
                title = getString(R.string.all_report_duplicate_dialog_title),
                message = getString(R.string.all_report_duplicate_message),
                buttonLabel = getString(R.string.all_okay),
            ).show()

            RecruitmentPostDetailUiEvent.ReportFail -> binding.root.showSnackBar(getString(R.string.all_report_fail_message))
        }
    }

    companion object {
        fun startActivity(
            context: Context,
            eventId: Long,
            recruitmentId: Long,
        ) {
            Intent(context, RecruitmentDetailActivity::class.java)
                .putExtra(EVENT_ID_KEY, eventId)
                .putExtra(RECRUITMENT_ID_KEY, recruitmentId)
                .run { context.startActivity(this) }
        }
    }
}
