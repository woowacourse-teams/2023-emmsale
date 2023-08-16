package com.emmsale.presentation.ui.eventdetail.recruitment.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityRecruitmentPostDetailBinding
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.views.InfoDialog
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.BottomMenuDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.MenuItemType
import com.emmsale.presentation.ui.eventdetail.recruitment.detail.uiState.RecruitmentPostDetailEvent
import com.emmsale.presentation.ui.eventdetail.recruitment.writing.RecruitmentPostWritingActivity
import com.emmsale.presentation.ui.profile.ProfileActivity

class RecruitmentPostDetailActivity :
    AppCompatActivity(),
    RequestCompanionFragmentDialog.RequestCompanionDialogListener {
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
    private val postEditorDialog: BottomMenuDialog by lazy {
        BottomMenuDialog(this).apply {
            addMenuItemBelow(
                title = getString(R.string.recruitmentpostdetail_dialog_editing_text),
                onClick = { navigateToEditPage() },
            )

            addMenuItemBelow(
                title = getString(R.string.recruitmentpostdetail_dialog_deletion_text),
                onClick = { showDeleteDialog() },
            )
        }
    }

    private fun showDeleteDialog() {
        WarningDialog(
            context = this,
            title = "모집 글 삭제",
            message = "해당 모집 글을 삭제 하시겠습니까?",
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
            title = "신고하기",
            message = "해당 모집 글을 신고 하시겠습니까?",
            positiveButtonLabel = getString(R.string.all_report_dialog_positive_button_label),
            negativeButtonLabel = getString(R.string.all_cancel),
            onPositiveButtonClick = { viewModel.reportRecruitment() },
        ).show()
    }

    private val postingResultActivityLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result == null || result.resultCode != RESULT_OK) return@registerForActivityResult
            viewModel.fetchRecruitmentPost()
        }

    override fun getReceiverName(): String = viewModel.recruitmentPost.value.name

    override fun onPositiveButtonClick(content: String): Unit =
        viewModel.requestCompanion(message = content)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initClickListener()
        setUpCompanionRequest()
        setUpIsPostDeleteSuccess()
        setupEventUiLogic()
    }

    private fun initBinding() {
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.vm = viewModel
    }

    private fun initClickListener() {
        initOptionButtonClick()
        initRequestCompanionButtonClick()
        initBackPressButtonClick()
        initBackPressIconClick()
        initProfileClick()
    }

    private fun setUpCompanionRequest() {
        viewModel.companionRequest.observe(this) { companionRequest ->
            when {
                companionRequest.isSuccess -> showToast(getString(R.string.recruitmentpostdetail_success_request_message))
                companionRequest.isError -> showToast(getString(R.string.recruitmentpostdetail_fail_request_message))
            }
        }
    }

    private fun setUpIsPostDeleteSuccess() {
        viewModel.isDeletePostSuccess.observe(this) { isPostDeleteSuccess ->
            if (isPostDeleteSuccess) {
                showToast(getString(R.string.recruitmentpostdetail_deletion_success_message))
                onBackPressedDispatcher.onBackPressed()
            } else {
                showToast(getString(R.string.recruitmentpostdetail_deletion_fail_message))
            }
        }
    }

    private fun setupEventUiLogic() {
        viewModel.event.observe(this) {
            handleEvent(it)
        }
    }

    private fun initRequestCompanionButtonClick() {
        binding.btnRecruitmentdetailRequestCompanion.setOnClickListener {
            showRequestCompanionDialog()
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

    private fun handleEvent(event: RecruitmentPostDetailEvent?) {
        if (event == null) return
        when (event) {
            RecruitmentPostDetailEvent.REPORT_FAIL -> showToast(getString(R.string.all_report_fail_message))
            RecruitmentPostDetailEvent.REPORT_SUCCESS -> InfoDialog(
                context = this,
                title = getString(R.string.all_report_complete_dialog_title),
                message = getString(R.string.all_report_complete_dialog_message),
                buttonLabel = getString(R.string.all_okay),
            ).show()
        }
        viewModel.removeEvent()
    }

    private fun finishWithResult() {
        setResult(RESULT_OK)
        finish()
    }

    private fun showRequestCompanionDialog() {
        RequestCompanionFragmentDialog().show(supportFragmentManager, "")
    }

    private fun navigateToEditPage() {
        val intent = RecruitmentPostWritingActivity.getEditModeIntent(
            this,
            eventId,
            recruitmentId,
            viewModel.recruitmentPost.value.content,
        )
        postingResultActivityLauncher.launch(intent)
    }

    companion object {
        private const val EVENT_ID_KEY = "EVENT_ID_KEY"
        private const val RECRUITMENT_ID_KEY = "RECRUITMENT_ID_KEY"
        private const val DEFAULT__ID = -1L

        fun getIntent(
            context: Context,
            eventId: Long,
            recruitmentId: Long,
        ): Intent {
            val intent = Intent(context, RecruitmentPostDetailActivity::class.java)
            intent.putExtra(EVENT_ID_KEY, eventId)
            intent.putExtra(RECRUITMENT_ID_KEY, recruitmentId)
            return intent
        }
    }
}
