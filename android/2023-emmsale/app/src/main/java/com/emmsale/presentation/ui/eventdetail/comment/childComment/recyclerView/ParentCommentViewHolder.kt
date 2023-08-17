package com.emmsale.presentation.ui.eventdetail.comment.childComment.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R
import com.emmsale.databinding.ItemChildcommentsParentcommentBinding
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.BottomMenuDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.MenuItemType
import com.emmsale.presentation.ui.eventdetail.comment.childComment.uiState.CommentUiState

class ParentCommentViewHolder(
    private val binding: ItemChildcommentsParentcommentBinding,
    private val showProfile: (authorId: Long) -> Unit,
    private val editComment: (commentId: Long) -> Unit,
    private val deleteComment: (commentId: Long) -> Unit,
    private val reportComment: (commentId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    private val bottomMenuDialog = BottomMenuDialog(binding.root.context)

    init {
        binding.ivChildcommentsParentCommentAuthorImage.setOnClickListener {
            if (binding.comment?.isDeleted == true) return@setOnClickListener
            showProfile(binding.comment?.authorId ?: return@setOnClickListener)
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
        if (comment.isReportable) bottomMenuDialog.addReportButton()

        binding.ivChildcommentsParentcommentmenubutton.setOnClickListener { bottomMenuDialog.show() }
    }

    private fun BottomMenuDialog.addUpdateButton() {
        addMenuItemBelow(context.getString(R.string.all_update_button_label)) {
            editComment(binding.comment?.id ?: return@addMenuItemBelow)
        }
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
                deleteComment(binding.comment?.id ?: return@WarningDialog)
            },
        ).show()
    }

    private fun BottomMenuDialog.addReportButton() {
        addMenuItemBelow(
            context.getString(R.string.all_report_button_label),
            MenuItemType.IMPORTANT,
        ) { reportComment(binding.comment?.id ?: return@addMenuItemBelow) }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            showProfile: (authorId: Long) -> Unit,
            updateComment: (commentId: Long) -> Unit,
            deleteComment: (commendId: Long) -> Unit,
            reportComment: (commentId: Long) -> Unit,
        ): ParentCommentViewHolder {
            val binding = ItemChildcommentsParentcommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )

            return ParentCommentViewHolder(
                binding,
                showProfile,
                updateComment,
                deleteComment,
                reportComment,
            )
        }
    }
}