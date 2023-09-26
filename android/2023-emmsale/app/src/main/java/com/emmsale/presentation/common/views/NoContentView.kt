package com.emmsale.presentation.common.views

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.emmsale.R
import com.emmsale.databinding.LayoutNoContentBinding

class NoContentView : ConstraintLayout {
    private val binding by lazy {
        LayoutNoContentBinding.inflate(LayoutInflater.from(context), this, false)
    }

    init {
        addView(binding.root)
        isClickable = true
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(attrs)
    }

    private fun initView(
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) {
        initResources(attrs, defStyleAttr)
    }

    private fun initResources(attrs: AttributeSet?, defStyleAttr: Int) {
        context.obtainStyledAttributes(
            attrs,
            R.styleable.NoContentView,
            defStyleAttr,
            0,
        ).use {
            initView(it)
        }
    }

    private fun initView(typedArray: TypedArray): Unit = with(typedArray) {
        val imageResId = getResourceId(R.styleable.NoContentView_noContentImageResId, 0)
        val imageColor = getResourceId(R.styleable.NoContentView_noContentImageTint, NO_TINT_ID)
        val title = getString(R.styleable.NoContentView_title) ?: ""
        val description = getString(R.styleable.NoContentView_description) ?: ""

        binding.tvTitle.text = title
        binding.tvDescription.text = description
        Glide.with(context).load(imageResId).into(binding.ivNoContent)
        if (imageColor != NO_TINT_ID) {
            binding.ivNoContent.setColorFilter(ContextCompat.getColor(context, imageColor))
        }
    }

    companion object {
        private const val NO_TINT_ID = -1
    }
}
