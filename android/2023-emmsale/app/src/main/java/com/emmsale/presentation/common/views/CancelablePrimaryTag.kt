package com.emmsale.presentation.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import androidx.annotation.Dimension
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.emmsale.R

class CancelablePrimaryTag(
    context: Context,
    attrs: AttributeSet? = null,
    onCancel: () -> Unit = {},
    parent: ViewGroup? = null,
) : AppCompatTextView(context, attrs) {
    constructor(context: Context) : this(context, null, {})

    init {
        initView()
        initCancelClickListener(parent, onCancel)
    }

    private fun initView() {
        initTextView(context)
        initBackground(context)
        drawCancelIcon()
    }

    private fun initTextView(context: Context) {
        setTextSize(Dimension.SP, 13F)
        setTextColor(ContextCompat.getColor(context, R.color.black))
    }

    private fun initBackground(context: Context) {
        gravity = Gravity.CENTER
        background = ContextCompat.getDrawable(context, R.drawable.bg_cancelable_primary_tag)
    }

    private fun drawCancelIcon() {
        val cancelIcon = ContextCompat.getDrawable(context, R.drawable.ic_cancelable_close)

        setCompoundDrawables(null, null, cancelIcon, null)
        compoundDrawablePadding = DRAWABLE_PADDING_SIZE
    }

    private fun initCancelClickListener(parent: ViewGroup?, onCancel: () -> Unit) {
        setOnClickListener {
            parent?.removeView(this)
            onCancel()
        }
    }

    companion object {
        private const val DRAWABLE_PADDING_SIZE = 10
    }
}

fun Context.cancelablePrimaryChipOf(
    parent: ViewGroup? = null,
    onCancel: () -> Unit = {},
    block: CancelablePrimaryTag.() -> Unit = {},
): CancelablePrimaryTag = CancelablePrimaryTag(
    context = this,
    parent = parent,
    onCancel = onCancel,
).apply(block)