package com.emmsale.presentation.common.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.emmsale.R
import com.emmsale.databinding.DialogBottomMenuBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class BottomMenuDialog(
    context: Context,
) : BottomSheetDialog(context) {

    private val binding: DialogBottomMenuBinding by lazy {
        DialogBottomMenuBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initDialogWindow()
    }

    fun addMenuItemBelow(
        title: String,
        menuItemType: Int = BottomDialogMenuItem.NORMAL,
        onClick: () -> Unit,
    ) {
        if (binding.llBottommenudialogMenuitems.childCount > 0) {
            binding.llBottommenudialogMenuitems.addView(VerticalDivider(context))
        }
        binding.llBottommenudialogMenuitems.addView(createMenuItem(title, menuItemType, onClick))
    }

    private fun createMenuItem(
        title: String,
        menuItemType: Int,
        onClick: () -> Unit,
    ): BottomDialogMenuItem {
        return BottomDialogMenuItem(context).apply {
            text = title
            setTextColor(getTitleTextColorOf(menuItemType))
            setOnClickListener {
                onClick()
                dismiss()
            }
        }
    }

    private fun getTitleTextColorOf(menuItemType: Int): Int = when (menuItemType) {
        BottomDialogMenuItem.NORMAL -> ContextCompat.getColor(context, R.color.black)
        BottomDialogMenuItem.DANGER -> ContextCompat.getColor(context, R.color.important)
        else -> throw IllegalArgumentException("${menuItemType}은 존재하지 않는 메뉴 아이템 타입입니다.")
    }

    private fun initDialogWindow() {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.attributes?.let {
            it.width = ViewGroup.LayoutParams.MATCH_PARENT
            it.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }
}
