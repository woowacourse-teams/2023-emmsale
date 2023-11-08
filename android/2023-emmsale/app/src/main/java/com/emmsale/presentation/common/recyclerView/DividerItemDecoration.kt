package com.emmsale.presentation.common.recyclerView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R
import com.emmsale.presentation.common.extension.dp

class DividerItemDecoration(
    context: Context,
    private val dividerHeight: Float = 0.5f.dp,
    @ColorRes private val dividerColor: Int = R.color.light_gray,
) : RecyclerView.ItemDecoration() {
    private val paint = Paint().apply {
        color = ContextCompat.getColor(context, dividerColor)
        style = Paint.Style.FILL
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        val left = parent.paddingStart.toFloat()
        val right = parent.width - parent.paddingEnd.toFloat()
        for (i in 0 until parent.childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom.toFloat() + params.bottomMargin
            val bottom = top + dividerHeight

            val dividerRect = RectF(left, top, right, bottom)
            c.drawRect(dividerRect, paint)
        }
    }
}
