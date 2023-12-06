package com.emmsale.presentation.ui.recruitmentWriting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.emmsale.R
import com.emmsale.databinding.ActivityRecruitmentPostWritingBinding
import com.emmsale.presentation.base.NetworkActivity
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.ui.recruitmentDetail.RecruitmentPostDetailActivity
import com.emmsale.presentation.ui.recruitmentList.uiState.WritingModeUiState.EDIT
import com.emmsale.presentation.ui.recruitmentList.uiState.WritingModeUiState.POST
import com.emmsale.presentation.ui.recruitmentWriting.RecruitmentPostWritingViewModel.Companion.EVENT_ID_KEY
import com.emmsale.presentation.ui.recruitmentWriting.RecruitmentPostWritingViewModel.Companion.RECRUITMENT_CONTENT_KEY
import com.emmsale.presentation.ui.recruitmentWriting.RecruitmentPostWritingViewModel.Companion.RECRUITMENT_ID_KEY
import com.emmsale.presentation.ui.recruitmentWriting.uiState.RecruitmentPostWritingUiEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecruitmentPostWritingActivity :
    NetworkActivity<ActivityRecruitmentPostWritingBinding>(R.layout.activity_recruitment_post_writing) {

    override val viewModel: RecruitmentPostWritingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupDataBinding()
        setupToolbar()
        observeUiEvent()
    }

    private fun setupDataBinding() {
        setContentView(binding.root)
        binding.vm = viewModel
    }

    private fun setupToolbar() {
        binding.tbToolbar.setNavigationOnClickListener { finish() }

        setupMenuText()
        setupMenuClickListener()
    }

    private fun setupMenuText() {
        binding.tbToolbar.menu.clear()
        when (viewModel.writingMode) {
            POST -> binding.tbToolbar.inflateMenu(R.menu.menu_postwriting_toolbar)
            EDIT -> {
                binding.tbToolbar.inflateMenu(R.menu.menu_postedit_toolbar)
                binding.etRecruitmentwriting.setText(viewModel.recruitmentContentToEdit)
            }
        }
    }

    private fun setupMenuClickListener() {
        binding.tbToolbar.setOnMenuItemClickListener {
            val content = binding.etRecruitmentwriting.text.toString()
            if (content.isEmpty()) {
                binding.root.showSnackBar(getString(R.string.recruitmentpostwriting_no_content_error_message))
                return@setOnMenuItemClickListener true
            }
            if (!viewModel.canPost.value) return@setOnMenuItemClickListener true
            when (viewModel.writingMode) {
                POST -> viewModel.postRecruitment(content)
                EDIT -> viewModel.editRecruitment(content)
            }
            true
        }
    }

    private fun observeUiEvent() {
        viewModel.uiEvent.observe(this) {
            handleUiEvent(it)
        }
    }

    private fun handleUiEvent(uiEvent: RecruitmentPostWritingUiEvent) {
        when (uiEvent) {
            RecruitmentPostWritingUiEvent.EditComplete -> {
                setResult(RESULT_OK)
                finish()
            }

            RecruitmentPostWritingUiEvent.EditFail -> binding.root.showSnackBar(R.string.recruitmentpostwriting_edit_fail_message)
            is RecruitmentPostWritingUiEvent.PostComplete -> {
                RecruitmentPostDetailActivity.startActivity(
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
            val intent = Intent(context, RecruitmentPostWritingActivity::class.java)
            intent.putExtra(EVENT_ID_KEY, eventId)
            return intent
        }

        fun getEditModeIntent(
            context: Context,
            eventId: Long,
            recruitmentId: Long,
            recruitmentContent: String,
        ): Intent {
            val intent = Intent(context, RecruitmentPostWritingActivity::class.java)
            intent.putExtra(EVENT_ID_KEY, eventId)
            intent.putExtra(RECRUITMENT_ID_KEY, recruitmentId)
            intent.putExtra(RECRUITMENT_CONTENT_KEY, recruitmentContent)
            return intent
        }
    }
}
