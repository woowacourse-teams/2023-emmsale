package com.emmsale.presentation.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import androidx.databinding.BindingAdapter
import com.emmsale.R
import com.emmsale.databinding.LayoutBasicInputWindowBinding
import com.emmsale.presentation.common.extension.dp
import com.emmsale.presentation.common.views.BasicTextInputWindow.OnSubmitListener
import kotlin.properties.Delegates.observable

class BasicTextInputWindow @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding: LayoutBasicInputWindowBinding by lazy {
        LayoutBasicInputWindowBinding.inflate(LayoutInflater.from(context), this, false)
    }

    var isSubmitEnabled: Boolean by observable(false) { _, _, newValue ->
        binding.isSubmitEnabled = newValue
    }

    var onSubmitListener: OnSubmitListener by observable(OnSubmitListener { }) { _, _, newValue ->
        binding.onSubmitListener = newValue
    }

    init {
        applyStyledAttributes(attrs)
        setPadding(17.dp, 8.dp, 17.dp, 8.dp)
        isClickable = true
        addView(binding.root)
    }

    private fun applyStyledAttributes(attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.BasicTextInputWindow,
            0,
            0,
        ).use {
            binding.etBasicInput.hint = it.getString(R.styleable.BasicTextInputWindow_hint)
            binding.tvSubmitButton.text =
                it.getString(R.styleable.BasicTextInputWindow_submitButtonLabel)
        }
    }

    fun clearText() {
        binding.etBasicInput.text.clear()
    }

    fun interface OnSubmitListener {
        fun onSubmit(text: String)
    }
}

@BindingAdapter("app:onSubmit")
fun BasicTextInputWindow.setOnSubmitListener(onSubmitListener: OnSubmitListener) {
    this.onSubmitListener = onSubmitListener
}

@BindingAdapter("app:isSubmitEnabled")
fun BasicTextInputWindow.setIsSubmitEnabled(isSubmitEnabled: Boolean) {
    this.isSubmitEnabled = isSubmitEnabled
}
