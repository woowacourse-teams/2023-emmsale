package com.emmsale.presentation.ui.eventdetail.comment.recyclerView

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R

class CommentRecyclerViewDivider(context: Context) : RecyclerView.ItemDecoration() {
    private val divider: Drawable by lazy {
        ContextCompat.getDrawable(context, R.drawable.bg_all_divider)
            ?: throw IllegalStateException("bg_all_divider 리소스를 찾을 수 없습니다. drawable 리소스를 확인해주세요.")
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        val left = parent.paddingStart
        val right = parent.width - parent.paddingRight

        for (i in 0 until parent.childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom = top + (divider.intrinsicHeight)

            divider.setBounds(left, top, right, bottom)
            divider.draw(c)
        }
    }
}
