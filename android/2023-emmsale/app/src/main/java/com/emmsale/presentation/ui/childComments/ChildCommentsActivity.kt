package com.emmsale.presentation.ui.childComments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import com.emmsale.R
import com.emmsale.databinding.ActivityChildCommentsBinding
import com.emmsale.databinding.DialogCommentDeleteBinding
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.ui.childComments.adapter.ChildCommentsAdapter
import com.emmsale.presentation.ui.childComments.uiState.ChildCommentsScreenUiState
import com.emmsale.presentation.ui.login.LoginActivity

class ChildCommentsActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityChildCommentsBinding.inflate(layoutInflater)
    }

    private val viewModel: ChildCommentsViewModel by viewModels {
        ChildCommentsViewModel.factory
    }

    private val eventId: Long by lazy { intent.getLongExtra(KEY_EVENT_ID, -1) }

    private val parentCommentId: Long by lazy { intent.getLongExtra(KEY_PARENT_COMMENT_ID, -1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initDataBinding()
        initChildCommentsRecyclerView()
        setUpUiLogic()

        viewModel.fetchComment(parentCommentId)
    }

    private fun initDataBinding() {
        binding.viewModel = viewModel
        binding.ivChildcommentsParentcommentdeletebutton.setOnClickListener { onParentCommentDeleteButtonClick() }
    }

    private fun initChildCommentsRecyclerView() {
        binding.rvChildcommentsChildcomments.apply {
            adapter = ChildCommentsAdapter(::onChildCommentDelete)
            itemAnimator = null
            addItemDecoration(
                DividerItemDecoration(
                    this@ChildCommentsActivity, DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun setUpUiLogic() {
        viewModel.uiState.observe(this) {
            handleParentComment(it)
            handleError(it)
            handleNotLogin(it)
            handleChildComments(it)
            handleEditComment()
            handleUpButton()
        }
    }

    private fun handleParentComment(childCommentsScreenUiState: ChildCommentsScreenUiState) {
        binding.progressBar.isVisible = childCommentsScreenUiState.isLoading
        binding.tvChildcommentsParentcommentauthorname.text = if (childCommentsScreenUiState.parentComment.isDeleted.not()) childCommentsScreenUiState.parentComment.authorName else getString(R.string.comment_deletedCommentAuthorName)
        binding.tvChildcommentsParentcommentcontent.text = childCommentsScreenUiState.parentComment.content
        binding.tvChildcommentsParentcommentlastmodifieddate.text = childCommentsScreenUiState.parentComment.lastModifiedDate
        binding.tvChildcommentsParentcommentlastmodifieddate.isVisible = childCommentsScreenUiState.parentComment.isDeleted.not()
        binding.ivChildcommentsParentcommentdeletebutton.isVisible = !childCommentsScreenUiState.parentComment.isDeleted && childCommentsScreenUiState.parentComment.isDeletable
        binding.tvChildcommentsParentcommentisupdated.isVisible = childCommentsScreenUiState.parentComment.isUpdated
    }

    private fun handleError(childCommentsScreenUiState: ChildCommentsScreenUiState) {
        if (childCommentsScreenUiState.isError) {
            this.showToast(childCommentsScreenUiState.errorMessage)
        }
    }

    private fun handleNotLogin(childCommentsScreenUiState: ChildCommentsScreenUiState) {
        if (childCommentsScreenUiState.isNotLogin) {
            LoginActivity.startActivity(this)
            finish()
        }
    }

    private fun handleChildComments(childCommentsScreenUiState: ChildCommentsScreenUiState) {
        (binding.rvChildcommentsChildcomments.adapter as ChildCommentsAdapter).submitList(
            childCommentsScreenUiState.childComments
        )
    }

    private fun handleEditComment() {
        binding.tvChildcommentsPostchildcommentbutton.setOnClickListener {
            onChildCommentSave()
        }
    }

    private fun handleUpButton() {
        binding.ivChildcommentsUpbutton.setOnClickListener {
            finish()
        }
    }

    private fun onChildCommentSave() {
        viewModel.saveChildComment(
            content = binding.etChildcommentsEditchildcommentcontent.text.toString(),
            parentCommentId = parentCommentId,
            eventId = eventId
        )
        binding.etChildcommentsEditchildcommentcontent.apply {
            text.clear()
        }
        this.showToast(getString(R.string.comments_completepostcomment))
    }

    private fun onChildCommentDelete(commentId: Long) {
        viewModel.deleteComment(commentId, parentCommentId)
        this.showToast(getString(R.string.comments_completedeletecomment))
    }

    private fun onParentCommentDeleteButtonClick() {
        val dialog = DialogCommentDeleteBinding.inflate(LayoutInflater.from(binding.root.context))

        val alertDialog = AlertDialog.Builder(binding.root.context)
            .setView(dialog.root)
            .create()

        dialog.tvCommentdeletedialogPositivebutton.setOnClickListener {
            viewModel.deleteComment(parentCommentId, parentCommentId)
            alertDialog.cancel()
        }

        dialog.tvCommentdeletedialogNegativebutton.setOnClickListener {
            alertDialog.cancel()
        }

        alertDialog?.window?.setBackgroundDrawable(ColorDrawable(0))

        alertDialog.show()
    }

    companion object {
        private const val KEY_EVENT_ID = "KEY_EVENT_ID"
        private const val KEY_PARENT_COMMENT_ID = "KEY_PARENT_COMMENT_ID"

        fun startActivity(context: Context, eventId: Long, parentCommentId: Long) {
            val intent = Intent(context, ChildCommentsActivity::class.java).apply {
                putExtra(KEY_EVENT_ID, eventId)
                putExtra(KEY_PARENT_COMMENT_ID, parentCommentId)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            context.startActivity(intent)
        }
    }
}