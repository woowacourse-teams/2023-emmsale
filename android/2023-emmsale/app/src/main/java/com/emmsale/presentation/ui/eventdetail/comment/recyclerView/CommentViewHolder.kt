package com.emmsale.presentation.ui.eventdetail.comment.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R
import com.emmsale.databinding.ItemCommentsCommentsBinding
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.ui.eventdetail.comment.uiState.CommentUiState

class CommentViewHolder(
    private val binding: ItemCommentsCommentsBinding,
    private val onChildCommentsView: (Long) -> Unit,
    private val onCommentDelete: (Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            onChildCommentsView(binding.comment?.commentId ?: return@setOnClickListener)
        }
        binding.ivCommentDeletebutton.setOnClickListener { onDeleteButtonClick() }
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
                onCommentDelete(binding.comment?.commentId ?: return@WarningDialog)
            },
        ).show()
    }

    fun bind(comment: CommentUiState) {
        binding.comment = comment
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onChildCommentsView: (Long) -> Unit,
            onCommentDelete: (Long) -> Unit,
        ): CommentViewHolder {
            val binding = ItemCommentsCommentsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )

            return CommentViewHolder(binding, onChildCommentsView, onCommentDelete)
        }
    }
}
