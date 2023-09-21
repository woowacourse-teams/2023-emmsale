package com.emmsale.presentation.ui.recruitmentDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityRecruitmentPostDetailBinding
import com.emmsale.presentation.common.Event
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.views.InfoDialog
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.BottomMenuDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.MenuItemType
import com.emmsale.presentation.ui.eventDetail.EventDetailActivity
import com.emmsale.presentation.ui.messageList.MessageListActivity
import com.emmsale.presentation.ui.profile.ProfileActivity
import com.emmsale.presentation.ui.recruitmentDetail.uiState.RecruitmentPostDetailUiEvent
import com.emmsale.presentation.ui.recruitmentWriting.RecruitmentPostWritingActivity

class RecruitmentPostDetailActivity : AppCompatActivity() {
    private val binding: ActivityRecruitmentPostDetailBinding by lazy {
        ActivityRecruitmentPostDetailBinding.inflate(layoutInflater)
    }
    private val viewModel: RecruitmentPostDetailViewModel by viewModels {
        RecruitmentPostDetailViewModel.factory(
            eventId,
            recruitmentId,
        )
    }
    private val eventId: Long by lazy {
        intent.getLongExtra(EVENT_ID_KEY, -DEFAULT__ID)
    }
    private val recruitmentId: Long by lazy {
        intent.getLongExtra(RECRUITMENT_ID_KEY, DEFAULT__ID)
    }
    private val isNavigatedFromMyPost: Boolean by lazy {
        intent.getBooleanExtra(FROM_MY_POST_KEY, false)
    }
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

    private val fetchByResultActivityLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result == null || result.resultCode != RESULT_OK) return@registerForActivityResult
            viewModel.refresh()
        }

    private fun showDeleteDialog() {
        WarningDialog(
            context = this,
            title = getString(R.string.recruitmentpostdetail_delete_dialog_title),
            message = getString(R.string.recruitmentpostdetail_delete_dialog_message),
            positiveButtonLabel = getString(R.string.all_delete_button_label),
            negativeButtonLabel = getString(R.string.all_delete_button_label),
            onPositiveButtonClick = { viewModel.deleteRecruitmentPost() },
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
        initBinding()
        initClickListener()
        setupEventUiLogic()
    }

    private fun initBinding() {
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        binding.isNavigatedFromMyPost = isNavigatedFromMyPost
    }

    private fun initClickListener() {
        initOptionButtonClick()
        initRequestCompanionButtonClick()
        initBackPressButtonClick()
        initBackPressIconClick()
        initProfileClick()
        initNavigateToEventDetailButtonClick()
    }

    private fun setupEventUiLogic() {
        viewModel.uiEvent.observe(this) {
            handleUiEvent(it)
        }
    }

    private fun initRequestCompanionButtonClick() {
        binding.btnRecruitmentdetailRequestCompanion.setOnClickListener {
            sendMessageDialog.show(supportFragmentManager, SendMessageDialog.TAG)
        }
    }

    private fun initOptionButtonClick() {
        binding.ivRecruitmentdetailOption.setOnClickListener {
            if (viewModel.recruitmentPost.value.isMyPost) {
                postEditorDialog.show()
            } else {
                postReportDialog.show()
            }
        }
    }

    private fun initNavigateToEventDetailButtonClick() {
        binding.btnRecruitmentdetailNavigateToEventDetail.setOnClickListener {
            EventDetailActivity.startActivity(this, eventId)
        }
    }

    private fun initBackPressButtonClick() {
        onBackPressedDispatcher.addCallback(this) {
            finishWithResult()
        }
    }

    private fun initBackPressIconClick() {
        binding.ivRecruitmentdetailBackpress.setOnClickListener {
            finishWithResult()
        }
    }

    private fun initProfileClick() {
        binding.ivRecruitmentdetailProfileImage.setOnClickListener {
            ProfileActivity.startActivity(this, viewModel.recruitmentPost.value.memberId)
        }
    }

    private fun handleUiEvent(event: Event<RecruitmentPostDetailUiEvent>) {
        val content = event.getContentIfNotHandled() ?: return
        when (content) {
            is RecruitmentPostDetailUiEvent.MessageSendComplete -> {
                MessageListActivity.startActivity(
                    this,
                    content.roomId,
                    content.otherId,
                )
                sendMessageDialog.dismiss()
            }

            RecruitmentPostDetailUiEvent.MessageSendFail -> binding.root.showSnackBar(R.string.sendmessagedialog_message_send_fail_message)
            RecruitmentPostDetailUiEvent.None -> {}
            RecruitmentPostDetailUiEvent.PostDeleteComplete -> {
                binding.root.showSnackBar(getString(R.string.recruitmentpostdetail_deletion_success_message))
                onBackPressedDispatcher.onBackPressed()
            }

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
            is RecruitmentPostDetailUiEvent.UnexpectedError -> showToast(content.errorMessage)
        }
    }

    private fun finishWithResult() {
        setResult(RESULT_OK)
        finish()
    }

    private fun navigateToEditPage() {
        val intent = RecruitmentPostWritingActivity.getEditModeIntent(
            this,
            eventId,
            recruitmentId,
            viewModel.recruitmentPost.value.content,
        )
        fetchByResultActivityLauncher.launch(intent)
    }

    companion object {
        private const val EVENT_ID_KEY = "EVENT_ID_KEY"
        private const val RECRUITMENT_ID_KEY = "RECRUITMENT_ID_KEY"
        private const val FROM_MY_POST_KEY = "FROM_MY_POST_KEY"
        private const val DEFAULT__ID = -1L

        fun getIntent(
            context: Context,
            eventId: Long,
            recruitmentId: Long,
            isNavigatedFromMyPost: Boolean = false,
        ): Intent {
            val intent = Intent(context, RecruitmentPostDetailActivity::class.java)
            intent.putExtra(EVENT_ID_KEY, eventId)
            intent.putExtra(RECRUITMENT_ID_KEY, recruitmentId)
            intent.putExtra(FROM_MY_POST_KEY, isNavigatedFromMyPost)
            return intent
        }
    }
}
