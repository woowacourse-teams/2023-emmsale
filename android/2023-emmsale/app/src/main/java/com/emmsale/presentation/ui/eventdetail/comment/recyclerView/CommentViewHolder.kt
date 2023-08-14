package com.emmsale.presentation.ui.eventdetail.comment.recyclerView

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.DialogCommentDeleteBinding
import com.emmsale.databinding.ItemCommentsCommentsBinding
import com.emmsale.presentation.ui.eventdetail.comment.uiState.CommentUiState

class CommentViewHolder(
    private val binding: ItemCommentsCommentsBinding,
    private val showChildComments: (parentCommentId: Long) -> Unit,
    private val deleteComment: (commentId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.tvCommentChildcommentscount.setOnClickListener {
            showChildComments(
                binding.comment?.commentId ?: return@setOnClickListener,
            )
        }
        binding.ivCommentDeletebutton.setOnClickListener { onDeleteButtonClick() }
    }

    private fun onDeleteButtonClick() {
        val dialog = DialogCommentDeleteBinding.inflate(LayoutInflater.from(binding.root.context))

        val alertDialog = AlertDialog.Builder(binding.root.context)
            .setView(dialog.root)
            .create()

        dialog.tvCommentdeletedialogPositivebutton.setOnClickListener {
            deleteComment(
                binding.comment?.commentId ?: return@setOnClickListener,
            )
            alertDialog.cancel()
        }

        dialog.tvCommentdeletedialogNegativebutton.setOnClickListener {
            alertDialog.cancel()
        }

        alertDialog?.window?.setBackgroundDrawable(ColorDrawable(0))

        alertDialog.show()
    }

    fun bind(comment: CommentUiState) {
        binding.comment = comment
    }

    companion object {
        fun create(
            parent: ViewGroup,
            showChildComments: (parentCommentId: Long) -> Unit,
            deleteComment: (commentId: Long) -> Unit,
        ): CommentViewHolder {
            val binding = ItemCommentsCommentsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )

            return CommentViewHolder(binding, showChildComments, deleteComment)
        }
    }
}
