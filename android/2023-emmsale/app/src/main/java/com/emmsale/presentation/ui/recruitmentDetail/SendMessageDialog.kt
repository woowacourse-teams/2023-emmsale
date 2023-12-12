package com.emmsale.presentation.ui.recruitmentDetail

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.emmsale.databinding.DialogRecruitmentdetailSendMessageBinding

class SendMessageDialog : DialogFragment() {

    private var _binding: DialogRecruitmentdetailSendMessageBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("쪽지 보내기 다이얼로그가 보이지 않을 때 바인딩 객체에 접근했습니다.")

    private val viewModel: RecruitmentDetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogRecruitmentdetailSendMessageBinding.inflate(inflater, container, false)
        setupWindow()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDataBinding()
    }

    private fun setupWindow() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setupDataBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel
        binding.onSendButtonClick = { viewModel.sendMessage(it) }
        binding.onCancelButtonClick = { dismiss() }
    }

    fun clearText() {
        binding.etSendmessagedialogMessage.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "TAG_SEND_MESSAGE_DIALOG"
    }
}
