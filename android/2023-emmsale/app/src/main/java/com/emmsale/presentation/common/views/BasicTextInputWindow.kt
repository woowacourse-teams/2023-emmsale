package com.emmsale.presentation.common.views

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.emmsale.R
import com.emmsale.databinding.LayoutBasicInputWindowBinding
import kotlin.properties.Delegates

class BasicTextInputWindow : ConstraintLayout {

    private val binding: LayoutBasicInputWindowBinding by lazy {
        LayoutBasicInputWindowBinding.inflate(LayoutInflater.from(context), this, false)
    }

    var isVisible: Boolean by Delegates.observable(false) { _, _, newValue ->
        binding.layoutTextInputWindow.isVisible = newValue
    }

    init {
        addView(binding.root)
        setupSubmitButton()
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
            binding.et.hint = it.getString(R.styleable.BasicTextInputWindow_hint)
            binding.tvSubmitButton.text =
                it.getString(R.styleable.BasicTextInputWindow_submitButtonLabel)
        }
    }

    private fun setupSubmitButton() {
        binding.et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tvSubmitButton.isEnabled = s?.isNotBlank() == true
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    fun setOnSubmitListener(onSubmitListener: OnSubmitListener) {
        binding.tvSubmitButton.setOnClickListener {
            onSubmitListener.onSubmit(binding.et.text.toString())
            binding.et.text.clear()
        }
    }

    interface OnSubmitListener {
        fun onSubmit(text: String)
    }
}

@BindingAdapter("app:isVisible")
fun BasicTextInputWindow.setIsVisible(isVisible: Boolean) {
    this.isVisible = isVisible
}

@BindingAdapter("app:onSubmit")
fun BasicTextInputWindow.setOnSubmitListenerBA(onSubmitListener: BasicTextInputWindow.OnSubmitListener) {
    setOnSubmitListener(onSubmitListener)
}
