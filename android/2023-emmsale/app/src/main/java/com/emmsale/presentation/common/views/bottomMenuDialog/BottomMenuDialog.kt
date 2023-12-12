package com.emmsale.presentation.common.views.bottomMenuDialog

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import com.emmsale.R
import com.emmsale.databinding.DialogBottomMenuBinding
import com.emmsale.presentation.common.views.HorizontalDivider
import com.google.android.material.bottomsheet.BottomSheetDialog

class BottomMenuDialog(
    context: Context,
) : BottomSheetDialog(context, R.style.RoundBottomSheetDialogStyle) {
    private val binding by lazy { DialogBottomMenuBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initDialogWindow()
    }

    private fun initDialogWindow() {
        window?.attributes?.let {
            it.width = ViewGroup.LayoutParams.MATCH_PARENT
            it.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }

    fun addMenuItemBelow(
        title: String,
        menuItemType: MenuItemType = MenuItemType.NORMAL,
        onClick: () -> Unit,
    ) {
        if (binding.llBottommenudialogMenuitems.childCount > 0) {
            binding.llBottommenudialogMenuitems.addView(HorizontalDivider(context))
        }
        binding.llBottommenudialogMenuitems.addView(createMenuItem(title, menuItemType, onClick))
    }

    fun resetMenu() {
        binding.llBottommenudialogMenuitems.removeAllViews()
    }

    private fun createMenuItem(
        title: String,
        menuItemType: MenuItemType,
        onClick: () -> Unit,
    ): BottomDialogMenuItem = BottomDialogMenuItem(context).apply {
        text = title
        setTextColor(menuItemType.getColor(context))
        setOnClickListener {
            onClick()
            dismiss()
        }
    }
}
