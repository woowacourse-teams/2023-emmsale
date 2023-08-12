package com.emmsale.presentation.ui.eventdetail.recruitment.writing

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityRecruitmentWritingBinding
import com.emmsale.presentation.common.extension.showToast

class RecruitmentWritingActivity : AppCompatActivity() {
    private val binding: ActivityRecruitmentWritingBinding by lazy {
        ActivityRecruitmentWritingBinding.inflate(layoutInflater)
    }
    private val eventId: Long by lazy {
        intent.getLongExtra(EVENT_ID_KEY, DEFAULT_ID_KEY)
    }
    private val viewModel: RecruitmentWritingViewModel by viewModels {
        RecruitmentWritingViewModel.factory(eventId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initRegisterButtonClickListener()
        initBackPressButtonClickListener()
        setUpIsSuccessRecruitmentPost()
    }

    private fun setUpIsSuccessRecruitmentPost() {
        viewModel.isSuccessPostRecruitment.observe(this) { isSuccess ->
            if (isSuccess) {
                showToast(getString(R.string.recruitmentwriting_register_success_message))
            } else {
                showToast(getString(R.string.recruitmentwriting_register_error_message))
            }
        }
    }

    private fun initBackPressButtonClickListener() {
        binding.ivRecruitmentwritingBackpress.setOnClickListener {
            finish()
        }
    }

    private fun initRegisterButtonClickListener() {
        with(binding.tvRecruitmentwritingRegister) {
            setOnClickListener {
                val content = text.toString()
                if (text.isEmpty()) {
                    showToast(context.getString(R.string.recruitmentwriting_no_content_error_message))
                } else {
                    viewModel.postRecruitment(content)
                }
            }
        }
    }

    companion object {
        private const val EVENT_ID_KEY = "EVENT_ID_KEY"
        private const val DEFAULT_ID_KEY = -1L
        fun startActivity(context: Context, eventId: Long) {
            val intent = Intent(context, RecruitmentWritingActivity::class.java)
            intent.putExtra(EVENT_ID_KEY, eventId)
            context.startActivity(intent)
        }
    }
}
