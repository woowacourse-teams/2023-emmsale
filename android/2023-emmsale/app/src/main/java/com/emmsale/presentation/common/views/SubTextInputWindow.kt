package com.emmsale.presentation.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import androidx.core.graphics.drawable.toDrawable
import androidx.databinding.BindingAdapter
import com.emmsale.R
import com.emmsale.databinding.LayoutSubTextInputWindowBinding
import com.emmsale.presentation.common.extension.dp
import com.emmsale.presentation.common.views.SubTextInputWindow.OnCancelListener
import com.emmsale.presentation.common.views.SubTextInputWindow.OnSubmitListener
import kotlin.properties.Delegates

class SubTextInputWindow @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding: LayoutSubTextInputWindowBinding by lazy {
        LayoutSubTextInputWindowBinding.inflate(LayoutInflater.from(context), this, false)
    }

    var text: String by Delegates.observable("") { _, _, newValue ->
        binding.etSubTextInput.setText(newValue)
        binding.etSubTextInput.setSelection(newValue.length)
    }

    var isVisible: Boolean by Delegates.observable(true) { _, _, newValue ->
        binding.isVisible = newValue
    }

    var isSubmitEnabled: Boolean by Delegates.observable(false) { _, _, newValue ->
        binding.isSubmitEnabled = newValue
    }

    var onSubmitListener: OnSubmitListener by Delegates.observable(OnSubmitListener { }) { _, _, newValue ->
        binding.onSubmitListener = newValue
    }

    var onCancelListener: OnCancelListener by Delegates.observable(OnCancelListener { }) { _, _, newValue ->
        binding.onCancelListener = newValue
    }

    init {
        applyStyledAttributes(attrs)
        addView(binding.root)
        binding.isVisible = isVisible
        background = context.getColor(R.color.white).toDrawable()
        elevation = 5.dp.toFloat()
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

@BindingAdapter("app:text")
fun SubTextInputWindow.setText(text: String?) {
    if (text != null) this.text = text
}

@BindingAdapter("app:visible")
fun SubTextInputWindow.setVisible(visible: Boolean) {
    this.isVisible = visible
}

@BindingAdapter("app:isSubmitEnabled")
fun SubTextInputWindow.setIsSubmitEnabled(isSubmitEnabled: Boolean) {
    this.isSubmitEnabled = isSubmitEnabled
}

@BindingAdapter("app:onSubmit")
fun SubTextInputWindow.setOnSubmitListener(onSubmitListener: OnSubmitListener) {
    this.onSubmitListener = onSubmitListener
}

@BindingAdapter("app:onCancel")
fun SubTextInputWindow.setOnCancelListener(onCancelListener: OnCancelListener) {
    this.onCancelListener = onCancelListener
}
