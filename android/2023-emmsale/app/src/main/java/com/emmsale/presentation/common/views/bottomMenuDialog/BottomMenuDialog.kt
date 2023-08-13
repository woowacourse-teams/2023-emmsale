package com.emmsale.presentation.common.views.bottomMenuDialog

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.emmsale.R
import com.emmsale.databinding.DialogBottomMenuBinding
import com.emmsale.presentation.common.views.VerticalDivider
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
        menuItemType: MenuItemType = MenuItemType.NORMAL,
        onClick: () -> Unit,
    ) {
        if (binding.llBottommenudialogMenuitems.childCount > 0) {
            binding.llBottommenudialogMenuitems.addView(VerticalDivider(context))
        }
        binding.llBottommenudialogMenuitems.addView(createMenuItem(title, menuItemType, onClick))
    }

    private fun createMenuItem(
        title: String,
        menuItemType: MenuItemType,
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

    private fun getTitleTextColorOf(menuItemType: MenuItemType): Int = when (menuItemType) {
        MenuItemType.NORMAL -> ContextCompat.getColor(context, R.color.black)
        MenuItemType.DANGER -> ContextCompat.getColor(context, R.color.red)
    }

    private fun initDialogWindow() {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.attributes?.let {
            it.width = ViewGroup.LayoutParams.MATCH_PARENT
            it.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }
}
