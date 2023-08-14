package com.emmsale.presentation.ui.eventdetail.comment.childComment.recyclerView

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.databinding.DialogCommentDeleteBinding
import com.emmsale.databinding.ItemChildcommentsChildcommentBinding
import com.emmsale.presentation.ui.eventdetail.comment.childComment.uiState.CommentUiState

class ChildCommentViewHolder(
    private val binding: ItemChildcommentsChildcommentBinding,
    private val deleteComment: (commentId: Long) -> Unit,
    private val showProfile: (authorId: Long) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.ivChildcommentDeletebutton.setOnClickListener { onDeleteButtonClick() }
        binding.root.setOnClickListener {
            showProfile(binding.comment?.authorId ?: return@setOnClickListener)
        }
    }

    private fun onDeleteButtonClick() {
        val dialog = DialogCommentDeleteBinding.inflate(LayoutInflater.from(binding.root.context))

        val alertDialog = AlertDialog.Builder(binding.root.context)
            .setView(dialog.root)
            .create()

        dialog.tvCommentdeletedialogPositivebutton.setOnClickListener {
            deleteComment(binding.comment?.commentId ?: return@setOnClickListener)
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
            deleteComment: (commentId: Long) -> Unit,
            showProfile: (authorId: Long) -> Unit,
        ): ChildCommentViewHolder {
            val binding = ItemChildcommentsChildcommentBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

            return ChildCommentViewHolder(binding, deleteComment, showProfile)
        }
    }
}
