package com.emmsale.presentation.ui.recruitmentWriting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityRecruitmentPostWritingBinding
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.ui.recruitmentDetail.RecruitmentPostDetailActivity
import com.emmsale.presentation.ui.recruitmentList.uiState.RecruitmentPostWritingUiState
import com.emmsale.presentation.ui.recruitmentList.uiState.WritingModeUiState
import com.emmsale.presentation.ui.recruitmentList.uiState.WritingModeUiState.EDIT
import com.emmsale.presentation.ui.recruitmentList.uiState.WritingModeUiState.POST
import com.emmsale.presentation.ui.recruitmentWriting.RecruitmentPostWritingViewModel.Companion.EVENT_ID_KEY
import com.emmsale.presentation.ui.recruitmentWriting.RecruitmentPostWritingViewModel.Companion.RECRUITMENT_CONTENT_KEY
import com.emmsale.presentation.ui.recruitmentWriting.RecruitmentPostWritingViewModel.Companion.RECRUITMENT_ID_KEY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecruitmentPostWritingActivity : AppCompatActivity() {
    private val binding by lazy { ActivityRecruitmentPostWritingBinding.inflate(layoutInflater) }
    private val viewModel: RecruitmentPostWritingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initCompleteButtonClick()
        initBackPressIconClick()
        setUpRecruitmentWriting()
        initBinding()
    }

    private fun initBinding() {
        setContentView(binding.root)
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    private fun setUpRecruitmentWriting() {
        viewModel.recruitmentWriting.observe(this) { recruitmentWriting ->
            onWritingResultStateChange(recruitmentWriting)
            changeMenuText(recruitmentWriting.writingMode)
        }
    }

    private fun onWritingResultStateChange(recruitmentWriting: RecruitmentPostWritingUiState) {
        when {
            recruitmentWriting.isPostingSuccess -> {
                showPostingSuccessMessage()
                navigateToPostedPage()
                setResult(RESULT_OK)
                finish()
            }

            recruitmentWriting.isEditingSuccess -> {
                showEditingSuccessMessage()
                setResult(RESULT_OK)
                finish()
            }

            recruitmentWriting.isError -> showPostingErrorMessage()
        }
    }

    private fun changeMenuText(writingMode: WritingModeUiState) {
        binding.tbToolbar.menu.clear()
        when (writingMode) {
            POST -> binding.tbToolbar.inflateMenu(R.menu.menu_postwriting_toolbar)

            EDIT -> {
                binding.tbToolbar.inflateMenu(R.menu.menu_postedit_toolbar)
                binding.etRecruitmentwriting.setText(viewModel.recruitmentContentToEdit)
            }
        }
    }

    private fun navigateToPostedPage() {
        RecruitmentPostDetailActivity.startActivity(
            context = this,
            eventId = viewModel.eventId,
            recruitmentId = viewModel.postedRecruitmentId.value,
        )
    }

    private fun showPostingErrorMessage() =
        binding.root.showSnackBar(getString(R.string.recruitmentpostwriting_register_error_message))

    private fun showPostingSuccessMessage() =
        binding.root.showSnackBar(getString(R.string.recruitmentpostwriting_register_success_message))

    private fun showEditingSuccessMessage() =
        binding.root.showSnackBar(getString(R.string.recruitmentpostwriting_edit_success_message))

    private fun initBackPressIconClick() {
        binding.tbToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initCompleteButtonClick() {
        binding.tbToolbar.setOnMenuItemClickListener {
            val content = binding.etRecruitmentwriting.text.toString()
            if (content.isEmpty()) {
                binding.root.showSnackBar(getString(R.string.recruitmentpostwriting_no_content_error_message))
            }
            when (viewModel.recruitmentWriting.value.writingMode) {
                EDIT -> viewModel.editRecruitment(content)
                POST -> viewModel.postRecruitment(content)
            }
            true
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
