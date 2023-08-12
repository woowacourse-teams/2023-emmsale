package com.emmsale.presentation.ui.eventdetail.comment.childComment.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R
import com.emmsale.databinding.ItemChildcommentsParentcommentBinding
import com.emmsale.presentation.common.views.BottomDialogMenuItem
import com.emmsale.presentation.common.views.BottomMenuDialog
import com.emmsale.presentation.common.views.DangerDialog
import com.emmsale.presentation.ui.eventdetail.comment.childComment.uiState.CommentUiState

class ParentCommentViewHolder(
    private val binding: ItemChildcommentsParentcommentBinding,
    private val onCommentDelete: (Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(comment: CommentUiState) {
        binding.comment = comment

        initMenuButton(comment)
    }

    private fun initMenuButton(comment: CommentUiState) {
        val bottomMenuDialog = BottomMenuDialog(binding.root.context)
        if (comment.isUpdatable) bottomMenuDialog.addUpdateButton()
        if (comment.isDeletable) bottomMenuDialog.addDeleteButton()
        bottomMenuDialog.addReportButton()

        binding.ivChildcommentsParentcommentmenubutton.setOnClickListener { bottomMenuDialog.show() }
    }

    private fun BottomMenuDialog.addUpdateButton() {
        addMenuItemBelow(context.getString(R.string.all_updateButtonLabel)) { }
    }

    private fun BottomMenuDialog.addDeleteButton() {
        addMenuItemBelow(context.getString(R.string.all_deleteButtonLabel)) { onDeleteButtonClick() }
    }

    private fun onDeleteButtonClick() {
        val context = binding.root.context
        DangerDialog(
            context = context,
            title = context.getString(R.string.commentdeletedialog_title),
            message = context.getString(R.string.commentdeletedialog_message),
            positiveButtonLabel = context.getString(R.string.commentdeletedialog_positive_button_label),
            negativeButtonLabel = context.getString(R.string.commentdeletedialog_negative_button_label),
            onPositiveButtonClick = {
                onCommentDelete(binding.comment?.commentId ?: return@DangerDialog)
            },
        ).show()
    }

    private fun BottomMenuDialog.addReportButton() {
        addMenuItemBelow(
            context.getString(R.string.all_reportButtonLabel),
            BottomDialogMenuItem.DANGER,
        ) { }
    }

    companion object {
        fun create(parent: ViewGroup, onCommentDelete: (Long) -> Unit): ParentCommentViewHolder {
            val binding = ItemChildcommentsParentcommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )

            return ParentCommentViewHolder(binding, onCommentDelete)
        }
    }
}
