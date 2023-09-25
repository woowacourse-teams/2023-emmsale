package com.emmsale.presentation.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.emmsale.R
import com.emmsale.databinding.LayoutNoContentBinding

class NoContentView : ConstraintLayout {
    private var imageResId: Int? = null
    private var imageColor: Int = NO_TINT_ID
    private var title: String = ""
    private var description: String = ""
    private val binding by lazy {
        LayoutNoContentBinding.bind(
            LayoutInflater.from(context).inflate(R.layout.layout_no_content, this, false),
        )
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
        initView()
    }

    private fun initResources(attrs: AttributeSet?, defStyleAttr: Int) {
        context.obtainStyledAttributes(
            attrs,
            R.styleable.NoContentView,
            defStyleAttr,
            0,
        ).use {
            imageResId = it.getResourceId(R.styleable.NoContentView_noContentImageResId, 0)
            imageColor = it.getResourceId(R.styleable.NoContentView_noContentImageTint, NO_TINT_ID)
            title = it.getString(R.styleable.NoContentView_title) ?: ""
            description = it.getString(R.styleable.NoContentView_description) ?: ""
        }
    }

    private fun initView() {
        Glide.with(context).load(imageResId).into(binding.ivNoContent)
        if (imageColor != NO_TINT_ID) {
            binding.ivNoContent.setColorFilter(ContextCompat.getColor(context, imageColor))
        }
        binding.tvTitle.text = title
        binding.tvDescription.text = description
    }

    companion object {
        private const val NO_TINT_ID = -1
    }
}
