package com.emmsale.presentation.ui.eventdetail.comment.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R
import com.emmsale.databinding.ItemCommentsCommentsBinding
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.BottomMenuDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.MenuItemType
import com.emmsale.presentation.ui.eventdetail.comment.uiState.CommentUiState

class CommentViewHolder(
    private val binding: ItemCommentsCommentsBinding,
    private val showChildComments: (parentCommentId: Long) -> Unit,
    private val deleteComment: (commentId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    private val bottomMenuDialog = BottomMenuDialog(binding.root.context)

    init {
        binding.root.setOnClickListener {
            showChildComments(binding.comment?.commentId ?: return@setOnClickListener)
        }
    }

    fun bind(comment: CommentUiState) {
        binding.comment = comment

        initMenuButton(comment)
    }

    private fun initMenuButton(comment: CommentUiState) {
        bottomMenuDialog.resetMenu()
        if (comment.isUpdatable) bottomMenuDialog.addUpdateButton()
        if (comment.isDeletable) bottomMenuDialog.addDeleteButton()
        bottomMenuDialog.addReportButton()

        binding.ivCommentMenubutton.setOnClickListener { bottomMenuDialog.show() }
    }

    private fun BottomMenuDialog.addUpdateButton() {
        addMenuItemBelow(context.getString(R.string.all_update_button_label)) { }
    }

    private fun BottomMenuDialog.addDeleteButton() {
        addMenuItemBelow(context.getString(R.string.all_delete_button_label)) { onDeleteButtonClick() }
    }

    private fun onDeleteButtonClick() {
        val context = binding.root.context
        WarningDialog(
            context = context,
            title = context.getString(R.string.commentdeletedialog_title),
            message = context.getString(R.string.commentdeletedialog_message),
            positiveButtonLabel = context.getString(R.string.commentdeletedialog_positive_button_label),
            negativeButtonLabel = context.getString(R.string.commentdeletedialog_negative_button_label),
            onPositiveButtonClick = {
                deleteComment(binding.comment?.commentId ?: return@WarningDialog)
            },
        ).show()
    }

    private fun BottomMenuDialog.addReportButton() {
        addMenuItemBelow(
            context.getString(R.string.all_report_button_label),
            MenuItemType.IMPORTANT,
        ) { }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            showChildComments: (parentCommentId: Long) -> Unit,
            deleteComment: (commentId: Long) -> Unit,
        ): CommentViewHolder {
            val binding = ItemCommentsCommentsBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

            return CommentViewHolder(binding, showChildComments, deleteComment)
        }
    }
}
