package com.emmsale.presentation.common.views

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import com.emmsale.databinding.DialogDangerBinding
import com.emmsale.presentation.common.extension.dp

class DangerDialog(
    context: Context,
    private val title: String,
    private val message: String,
    private val positiveButtonLabel: String,
    private val negativeButtonLabel: String,
    private val onPositiveButtonClick: () -> Unit = {},
    private val onNegativeButtonClick: () -> Unit = {},
) : Dialog(context) {

    private val binding: DialogDangerBinding by lazy {
        DialogDangerBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initDialogWindow()
        initDataBinding()
    }

    private fun initDialogWindow() {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.attributes?.let {
            it.width = 280.dp
            it.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }

    private fun initDataBinding() {
        binding.title = title
        binding.message = message
        binding.positiveButtonLabel = positiveButtonLabel
        binding.negativeButtonLabel = negativeButtonLabel
        binding.onPositiveButtonClick = {
            onPositiveButtonClick()
            dismiss()
        }
        binding.onNegativeButtonClick = {
            onNegativeButtonClick()
            dismiss()
        }
    }
}
