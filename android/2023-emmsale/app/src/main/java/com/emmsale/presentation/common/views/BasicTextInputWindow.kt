package com.emmsale.presentation.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import androidx.databinding.BindingAdapter
import com.emmsale.R
import com.emmsale.databinding.LayoutBasicInputWindowBinding
import com.emmsale.presentation.common.views.BasicTextInputWindow.OnSubmitListener
import kotlin.properties.Delegates

class BasicTextInputWindow : ConstraintLayout {

    private val binding: LayoutBasicInputWindowBinding by lazy {
        LayoutBasicInputWindowBinding.inflate(LayoutInflater.from(context), this, false)
    }

    var isVisible: Boolean by Delegates.observable(true) { _, _, newValue ->
        binding.isVisible = newValue
    }

    var isSubmitEnabled: Boolean by Delegates.observable(false) { _, _, newValue ->
        binding.isSubmitEnabled = newValue
    }

    var onSubmitListener: OnSubmitListener by Delegates.observable(OnSubmitListener { }) { _, _, newValue ->
        binding.tvSubmitButton.setOnClickListener {
            newValue.onSubmit(binding.etBasicInput.text.toString())
            binding.etBasicInput.text.clear()
        }
    }

    init {
        addView(binding.root)
        binding.isVisible = isVisible
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        applyStyledAttributes(attrs)
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

    fun interface OnSubmitListener {
        fun onSubmit(text: String)
    }
}

@BindingAdapter("app:visible")
fun BasicTextInputWindow.setIsVisible(isVisible: Boolean) {
    this.isVisible = isVisible
}

@BindingAdapter("app:onSubmit")
fun BasicTextInputWindow.setOnSubmitListenerBA(onSubmitListener: OnSubmitListener) {
    this.onSubmitListener = onSubmitListener
}

@BindingAdapter("app:isSubmitEnabled")
fun BasicTextInputWindow.setIsSubmitEnabled(isSubmitEnabled: Boolean) {
    this.isSubmitEnabled = isSubmitEnabled
}
