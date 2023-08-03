package com.emmsale.presentation.ui.comment.adpater

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.DialogCommentDeleteBinding
import com.emmsale.databinding.ItemCommentsCommentsBinding
import com.emmsale.presentation.ui.comment.uiState.CommentUiState

class CommentViewHolder(
    private val binding: ItemCommentsCommentsBinding,
    private val onChildCommentsView: (Long) -> Unit,
    private val onCommentDelete: (Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.tvCommentChildcommentscount.setOnClickListener {
            onChildCommentsView(
                binding.comment?.commentId ?: return@setOnClickListener
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
            onCommentDelete(
                binding.comment?.commentId ?: return@setOnClickListener
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
            onChildCommentsView: (Long) -> Unit,
            onCommentDelete: (Long) -> Unit,
        ): CommentViewHolder {
            val binding = ItemCommentsCommentsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            return CommentViewHolder(binding, onChildCommentsView, onCommentDelete)
        }
    }
}