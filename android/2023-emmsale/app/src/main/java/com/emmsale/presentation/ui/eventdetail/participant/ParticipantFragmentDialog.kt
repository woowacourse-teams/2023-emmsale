package com.emmsale.presentation.ui.eventdetail.participant

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.emmsale.databinding.FragmentdialogRecruitmentBinding

class ParticipantFragmentDialog(
    private val memberName: String,
    private val memberId: Long,
    private val requestCompanion: (Long, String) -> Unit,
) : DialogFragment() {
    private lateinit var binding: FragmentdialogRecruitmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentdialogRecruitmentBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.name = memberName
        positiveButtonClick()
        negativeButtonClick()
    }

    private fun positiveButtonClick() {
        binding.btnFragmentdialogPositive.setOnClickListener {
            val message = binding.edittextFragmentdialogRecruitmentMessage.text.toString()
            requestCompanion(memberId, message)
            dismiss()
        }
    }

    private fun negativeButtonClick() {
        binding.btnFragmentdialogNegative.setOnClickListener {
            dismiss()
        }
    }
}
