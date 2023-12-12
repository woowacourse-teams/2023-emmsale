package com.emmsale.presentation.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import androidx.core.graphics.drawable.toDrawable
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import com.emmsale.R
import com.emmsale.databinding.LayoutSubTextInputWindowBinding
import com.emmsale.presentation.common.extension.dp

@BindingMethods(
    BindingMethod(
        type = SubTextInputWindow::class,
        attribute = "text",
        method = "setText",
    ),
    BindingMethod(
        type = SubTextInputWindow::class,
        attribute = "isSubmitEnabled",
        method = "setIsSubmitEnabled",
    ),
    BindingMethod(
        type = SubTextInputWindow::class,
        attribute = "onSubmit",
        method = "setOnSubmitListener",
    ),
    BindingMethod(
        type = SubTextInputWindow::class,
        attribute = "onCancel",
        method = "setOnCancelListener",
    ),
)
class SubTextInputWindow @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding: LayoutSubTextInputWindowBinding by lazy {
        LayoutSubTextInputWindowBinding.inflate(LayoutInflater.from(context), this, false)
    }

    init {
        applyStyledAttributes(attrs)
        addView(binding.root)
        background = context.getColor(R.color.white).toDrawable()
        elevation = 5f.dp
    }

    private fun applyStyledAttributes(attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SubTextInputWindow,
            0,
            0,
        ).use {
            binding.tvSubmitButton.text =
                it.getString(R.styleable.SubTextInputWindow_submitButtonLabel)
            binding.tvCancelButton.text =
                it.getString(R.styleable.SubTextInputWindow_cancelButtonLabel)
        }
    }

    fun setText(text: String?) {
        if (text != null) binding.etSubTextInput.setText(text)
    }

    fun setIsSubmitEnabled(enabled: Boolean) {
        binding.isSubmitEnabled = enabled
    }

    fun setOnSubmitListener(onSubmitListener: OnSubmitListener) {
        binding.onSubmitListener = onSubmitListener
    }

    fun setOnCancelListener(onCancelListener: OnCancelListener) {
        binding.onCancelListener = onCancelListener
    }

    fun requestFocusOnEditText() {
        binding.etSubTextInput.requestFocus()
    }

    fun interface OnSubmitListener {
        fun onSubmit(text: String)
    }

    fun interface OnCancelListener {
        fun onCancel()
    }
}
