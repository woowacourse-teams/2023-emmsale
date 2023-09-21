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
import com.emmsale.presentation.ui.recruitmentList.uiState.WritingModeUiState.EDIT
import com.emmsale.presentation.ui.recruitmentList.uiState.WritingModeUiState.POST

class RecruitmentPostWritingActivity : AppCompatActivity() {
    private val binding: ActivityRecruitmentPostWritingBinding by lazy {
        ActivityRecruitmentPostWritingBinding.inflate(layoutInflater)
    }
    private val eventId: Long by lazy {
        intent.getLongExtra(EVENT_ID_KEY, DEFAULT_ID)
    }
    private val recruitmentIdToEdit: Long? by lazy {
        receiveRecruitmentId()
    }
    private val recruitmentContentToEdit: String by lazy {
        intent.getStringExtra(RECRUITMENT_CONTENT_KEY) ?: DEFAULT_CONTENT
    }
    private val viewModel: RecruitmentPostWritingViewModel by viewModels {
        RecruitmentPostWritingViewModel.factory(eventId, recruitmentIdToEdit)
    }

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
            onWritingModeStateChange(recruitmentWriting)
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

    private fun onWritingModeStateChange(recruitmentWriting: RecruitmentPostWritingUiState) {
        when (recruitmentWriting.writingMode) {
            POST -> {
                binding.tvRecruitmentwritingComplete.text =
                    getString(R.string.recruitmentpostwriting_register_button_text)
            }

            EDIT -> {
                binding.tvRecruitmentwritingComplete.text =
                    getString(R.string.recruitmentpostwriting_edit_button_text)
                binding.etRecruitmentwriting.setText(recruitmentContentToEdit)
            }
        }
    }

    private fun navigateToPostedPage() {
        startActivity(
            RecruitmentPostDetailActivity.getIntent(
                this,
                eventId,
                viewModel.postedRecruitmentId.value,
            ),
        )
    }

    private fun showPostingErrorMessage() =
        binding.root.showSnackBar(getString(R.string.recruitmentpostwriting_register_error_message))

    private fun showPostingSuccessMessage() =
        binding.root.showSnackBar(getString(R.string.recruitmentpostwriting_register_success_message))

    private fun showEditingSuccessMessage() =
        binding.root.showSnackBar(getString(R.string.recruitmentpostwriting_edit_success_message))

    private fun initBackPressIconClick() {
        binding.ivRecruitmentwritingBackpress.setOnClickListener {
            finish()
        }
    }

    private fun initCompleteButtonClick() {
        binding.tvRecruitmentwritingComplete.setOnClickListener {
            val content = binding.etRecruitmentwriting.text.toString()
            if (content.isEmpty()) {
                binding.root.showSnackBar(getString(R.string.recruitmentpostwriting_no_content_error_message))
                return@setOnClickListener
            }
            when (viewModel.recruitmentWriting.value.writingMode) {
                EDIT -> viewModel.editRecruitment(content)
                POST -> viewModel.postRecruitment(content)
            }
        }
    }

    private fun receiveRecruitmentId(): Long? {
        val recruitmentId = intent.getLongExtra(RECRUITMENT_ID_KEY, DEFAULT_ID)
        if (recruitmentId == DEFAULT_ID) return null
        return recruitmentId
    }

    companion object {
        private const val EVENT_ID_KEY = "EVENT_ID_KEY"
        private const val RECRUITMENT_ID_KEY = "RECRUITMENT_ID_KEY"
        private const val RECRUITMENT_CONTENT_KEY = "RECRUITMENT_CONTENT_KEY"
        private const val DEFAULT_CONTENT = ""
        private const val DEFAULT_ID = -1L

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