package com.emmsale.presentation.ui.recruitmentNotificationList

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.updateLayoutParams
import com.emmsale.R
import com.emmsale.databinding.DialogRecruitmentAcceptedBinding

class RecruitmentAcceptedDialog(
    context: Context,
) : Dialog(context, R.style.TranslucentStatusDialog) {
    private val binding: DialogRecruitmentAcceptedBinding by lazy {
        DialogRecruitmentAcceptedBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initDialogWindow()
        initDialog()
    }

    private fun initDialogWindow() {
        window?.setBackgroundDrawableResource(R.color.dialog_window_background_color)
        window?.attributes?.let {
            it.width = ViewGroup.LayoutParams.MATCH_PARENT
            it.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    }

    private fun initDialog() {
        initDialogSize()
        initDialogBackgroundSetting()
        initOkayClickListener()
    }

    private fun initDialogSize() {
        val deviceWidth = Resources.getSystem().displayMetrics.widthPixels

        binding.layoutRoot.updateLayoutParams {
            width = (deviceWidth * DEVICE_WIDTH_RATIO).toInt()
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }

    private fun initDialogBackgroundSetting() {
        setCanceledOnTouchOutside(false)
    }

    private fun initOkayClickListener() {
        binding.btnOkay.setOnClickListener { dismiss() }
    }

    companion object {
        private const val DEVICE_WIDTH_RATIO = 0.8F
    }
}
