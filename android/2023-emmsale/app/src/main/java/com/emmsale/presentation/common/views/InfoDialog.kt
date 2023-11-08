package com.emmsale.presentation.common.views

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import com.emmsale.R
import com.emmsale.databinding.DialogInfoBinding
import com.emmsale.presentation.common.extension.dp

class InfoDialog(
    context: Context,
    private val title: String,
    private val message: String,
    private val buttonLabel: String = context.getString(R.string.all_okay),
    private val onButtonClick: (() -> Unit)? = null,
    private val cancelable: Boolean = true,
) : Dialog(context) {
    private val binding: DialogInfoBinding by lazy { DialogInfoBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setCancelable(cancelable)
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
        binding.buttonLabel = buttonLabel
        binding.onButtonClick = {
            onButtonClick?.invoke()
            dismiss()
        }
    }
}
