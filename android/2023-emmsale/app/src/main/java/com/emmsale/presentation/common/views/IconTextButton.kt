package com.emmsale.presentation.common.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.emmsale.R

class IconTextButton : AppCompatButton {
    private var activeTextColor: Int = 0
    private var inactiveTextColor: Int = 0

    private var activeIconColor: Int = 0
    private var inactiveIconColor: Int = 0

    private var activeBackground: Int = 0
    private var inactiveBackground: Int = 0

    private var iconResId: Int? = null

    var isActive: Boolean = false
        set(newActiveState) {
            field = newActiveState
            invalidate()
        }

    constructor(context: Context) : super(context) {
        initViewAttrs(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs,
        R.attr.IconTextButtonStyle,
    ) {
        initViewAttrs(context, attrs, R.attr.IconTextButtonStyle)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr,
    ) {
        initViewAttrs(context, attrs, defStyleAttr)
    }

    private fun initViewAttrs(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) {
        context.obtainStyledAttributes(
            attrs,
            R.styleable.IconTextButton,
            defStyleAttr,
            0,
        ).use {
            iconResId = it.getResourceId(R.styleable.IconTextButton_icon, 0)

            activeTextColor = it.getColor(R.styleable.IconTextButton_activeTextColor, 0)
            inactiveTextColor = it.getColor(R.styleable.IconTextButton_inactiveTextColor, 0)

            activeIconColor = it.getColor(R.styleable.IconTextButton_activeIconColor, 0)
            inactiveIconColor = it.getColor(R.styleable.IconTextButton_inactiveIconColor, 0)

            activeBackground = it.getResourceId(R.styleable.IconTextButton_activeBackground, 0)
            inactiveBackground = it.getResourceId(R.styleable.IconTextButton_inactiveBackground, 0)

            isActive = it.getBoolean(R.styleable.IconTextButton_active, false)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        initIcon(context)
        updateActivationView()
    }

    private fun initIcon(context: Context) {
        if (iconResId != null && iconResId != 0) {
            val icon = ContextCompat.getDrawable(context, iconResId!!)
            setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
        }
    }

    private fun updateActivationView() {
        when (isActive) {
            true -> {
                setTextColor(activeTextColor)
                setBackgroundResource(activeBackground)
                if (compoundDrawables[0] != null) {
                    compoundDrawables[0].setTint(activeIconColor)
                }
            }

            false -> {
                setTextColor(inactiveTextColor)
                setBackgroundResource(inactiveBackground)
                if (compoundDrawables[0] != null) {
                    compoundDrawables[0].setTint(inactiveIconColor)
                }
            }
        }
    }
}
