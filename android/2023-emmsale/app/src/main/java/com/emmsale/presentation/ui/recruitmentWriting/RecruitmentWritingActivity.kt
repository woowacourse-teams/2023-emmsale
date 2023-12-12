package com.emmsale.presentation.ui.recruitmentWriting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.emmsale.R
import com.emmsale.databinding.ActivityRecruitmentWritingBinding
import com.emmsale.presentation.base.NetworkActivity
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.ui.recruitmentDetail.RecruitmentDetailActivity
import com.emmsale.presentation.ui.recruitmentList.uiState.WritingModeUiState.EDIT
import com.emmsale.presentation.ui.recruitmentList.uiState.WritingModeUiState.POST
import com.emmsale.presentation.ui.recruitmentWriting.RecruitmentPostWritingViewModel.Companion.EVENT_ID_KEY
import com.emmsale.presentation.ui.recruitmentWriting.RecruitmentPostWritingViewModel.Companion.RECRUITMENT_CONTENT_KEY
import com.emmsale.presentation.ui.recruitmentWriting.RecruitmentPostWritingViewModel.Companion.RECRUITMENT_ID_KEY
import com.emmsale.presentation.ui.recruitmentWriting.uiState.RecruitmentPostWritingUiEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecruitmentWritingActivity :
    NetworkActivity<ActivityRecruitmentWritingBinding>(R.layout.activity_recruitment_writing) {

    override val viewModel: RecruitmentPostWritingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupDataBinding()
        setupBackPressedDispatcher()
        setupToolbar()

        observeCanSubmit()
        observeUiEvent()
    }

    private fun setupDataBinding() {
        setContentView(binding.root)
        binding.vm = viewModel
    }

    private fun setupBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (viewModel.isChanged()) showFinishConfirmDialog() else finish()
                }
            },
        )
    }

    private fun showFinishConfirmDialog() {
        WarningDialog(
            context = this,
            title = getString(R.string.recruitmentpostwriting_writing_cancel_confirm_dialog_title),
            message = getString(R.string.recruitmentpostwriting_writing_cancel_confirm_dialog_message),
            positiveButtonLabel = getString(R.string.all_okay),
            negativeButtonLabel = getString(R.string.all_cancel),
            onPositiveButtonClick = { finish() },
        ).show()
    }

    private fun setupToolbar() {
        binding.tbToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        when (viewModel.writingMode) {
            POST -> binding.tbToolbar.inflateMenu(R.menu.menu_postwriting_toolbar)
            EDIT -> binding.tbToolbar.inflateMenu(R.menu.menu_postedit_toolbar)
        }
        setupMenuClickListener()
    }

    private fun setupMenuClickListener() {
        binding.tbToolbar.setOnMenuItemClickListener {
            val content = binding.etRecruitmentwriting.text.toString()
            when (viewModel.writingMode) {
                POST -> viewModel.postRecruitment(content)
                EDIT -> viewModel.editRecruitment(content)
            }
            true
        }
    }

    private fun observeCanSubmit() {
        viewModel.canSubmit.observe(this) { canSubmit ->
            binding.tbToolbar.menu.findItem(R.id.register).isEnabled = canSubmit
        }
    }

    private fun observeUiEvent() {
        viewModel.uiEvent.observe(this, ::handleUiEvent)
    }

    private fun handleUiEvent(uiEvent: RecruitmentPostWritingUiEvent) {
        when (uiEvent) {
            RecruitmentPostWritingUiEvent.EditComplete -> {
                setResult(RESULT_OK)
                finish()
            }

            RecruitmentPostWritingUiEvent.EditFail -> binding.root.showSnackBar(R.string.recruitmentpostwriting_edit_fail_message)
            is RecruitmentPostWritingUiEvent.PostComplete -> {
                RecruitmentDetailActivity.startActivity(
                    context = this,
                    eventId = viewModel.eventId,
                    recruitmentId = uiEvent.recruitmentId,
                )
                finish()
            }

            RecruitmentPostWritingUiEvent.PostFail -> binding.root.showSnackBar(R.string.recruitmentpostwriting_post_fail_message)
        }
    }

    companion object {
        fun getPostModeIntent(context: Context, eventId: Long): Intent {
            val intent = Intent(context, RecruitmentWritingActivity::class.java)
            intent.putExtra(EVENT_ID_KEY, eventId)
            return intent
        }

        fun getEditModeIntent(
            context: Context,
            eventId: Long,
            recruitmentId: Long,
            recruitmentContent: String,
        ): Intent {
            val intent = Intent(context, RecruitmentWritingActivity::class.java)
            intent.putExtra(EVENT_ID_KEY, eventId)
            intent.putExtra(RECRUITMENT_ID_KEY, recruitmentId)
            intent.putExtra(RECRUITMENT_CONTENT_KEY, recruitmentContent)
            return intent
        }
    }
}
