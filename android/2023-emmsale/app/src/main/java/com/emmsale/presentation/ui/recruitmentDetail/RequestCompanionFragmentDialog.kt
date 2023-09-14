package com.emmsale.presentation.ui.recruitmentDetail

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.emmsale.R
import com.emmsale.databinding.DialogRequestCompanionBinding

class RequestCompanionFragmentDialog : DialogFragment() {
    private lateinit var binding: DialogRequestCompanionBinding

    private lateinit var listener: RequestCompanionDialogListener

    interface RequestCompanionDialogListener {
        fun getReceiverName(): String
        fun onPositiveButtonClick(content: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as RequestCompanionDialogListener
        } catch (e: ClassCastException) {
            dismissNow()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogRequestCompanionBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNegativeButtonClick()
        initPositiveButtonClick()
        setDescriptionMessage()
    }

    private fun initNegativeButtonClick() {
        binding.btnRequestcompanionNegative.setOnClickListener {
            dismiss()
        }
    }

    private fun initPositiveButtonClick() {
        binding.btnRequestcompanionPositive.setOnClickListener {
            val content = binding.etRequestcompanionMessage.text.toString()
            listener.onPositiveButtonClick(content)
            dismiss()
        }
    }

    private fun setDescriptionMessage() {
        val receiverName = listener.getReceiverName()
        val spannableString = SpannableString(
            getString(
                R.string.requestcompanion_dialog_message_format,
                receiverName,
            ),
        )
        val color = ContextCompat.getColor(requireContext(), R.color.primary_color)
        spannableString.setSpan(ForegroundColorSpan(color), 0, receiverName.length, 0)
        binding.tvRequestcompanionDescription.text = spannableString
    }
}
