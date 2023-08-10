package com.emmsale.presentation.ui.eventdetail.comment.childComment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import com.emmsale.R
import com.emmsale.databinding.ActivityChildCommentsBinding
import com.emmsale.databinding.DialogCommentDeleteBinding
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.ui.eventdetail.comment.childComment.recyclerView.ChildCommentAdapter
import com.emmsale.presentation.ui.eventdetail.comment.childComment.uiState.ChildCommentsUiState
import com.emmsale.presentation.ui.login.LoginActivity

class ChildCommentActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityChildCommentsBinding.inflate(layoutInflater)
    }

    private val viewModel: ChildCommentViewModel by viewModels {
        ChildCommentViewModel.factory
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
        binding.ivChildcommentsParentcommentdeletebutton.setOnClickListener { onParentCommentDelete() }
    }

    private fun initChildCommentsRecyclerView() {
        binding.rvChildcommentsChildcomments.apply {
            adapter = ChildCommentAdapter(::onChildCommentDelete)
            itemAnimator = null
            addItemDecoration(
                DividerItemDecoration(
                    this@ChildCommentActivity,
                    DividerItemDecoration.VERTICAL,
                ),
            )
        }
    }

    private fun setUpUiLogic() {
        viewModel.isLogin.observe(this) {
            handleNotLogin(it)
        }
        viewModel.childCommentsUiState.observe(this) {
            handleParentComment(it)
            handleError(it)
            handleChildComments(it)
            handleEditComment()
            handleUpButton()
        }
    }

    private fun handleParentComment(childCommentsUiState: ChildCommentsUiState) {
        binding.progressBar.isVisible = childCommentsUiState.isLoading
        binding.tvChildcommentsParentcommentauthorname.text =
            if (childCommentsUiState.parentComment.isDeleted.not()) {
                childCommentsUiState.parentComment.authorName
            } else {
                getString(
                    R.string.comment_deleted_comment_author_name,
                )
            }
        binding.tvChildcommentsParentcommentcontent.text =
            childCommentsUiState.parentComment.content
        binding.tvChildcommentsParentcommentlastmodifieddate.text =
            childCommentsUiState.parentComment.lastModifiedDate
        binding.tvChildcommentsParentcommentlastmodifieddate.isVisible =
            childCommentsUiState.parentComment.isDeleted.not()
        binding.ivChildcommentsParentcommentdeletebutton.isVisible =
            !childCommentsUiState.parentComment.isDeleted && childCommentsUiState.parentComment.isDeletable
        binding.tvChildcommentsParentcommentisupdated.isVisible =
            childCommentsUiState.parentComment.isUpdated && childCommentsUiState.parentComment.isDeleted.not()
    }

    private fun handleError(childCommentsUiState: ChildCommentsUiState) {
        fun handleCommentsFetchingError(childCommentsUiState: ChildCommentsUiState) {
            if (childCommentsUiState.isFetchingError) {
                showToast(getString(R.string.comments_comments_fetching_error_message))
            }
        }

        fun handleCommentPostingError(childCommentsUiState: ChildCommentsUiState) {
            if (childCommentsUiState.isPostingError) {
                showToast(getString(R.string.comments_comments_posting_error_message))
            }
        }

        fun handleCommentDeletionError(childCommentsUiState: ChildCommentsUiState) {
            if (childCommentsUiState.isDeletionError) {
                showToast(getString(R.string.comments_comments_deletion_error_message))
            }
        }
        handleCommentsFetchingError(childCommentsUiState)
        handleCommentPostingError(childCommentsUiState)
        handleCommentDeletionError(childCommentsUiState)
    }

    private fun handleNotLogin(isLogin: Boolean) {
        if (!isLogin) {
            LoginActivity.startActivity(this)
            finish()
        }
    }

    private fun handleChildComments(childCommentsUiState: ChildCommentsUiState) {
        (binding.rvChildcommentsChildcomments.adapter as ChildCommentAdapter).submitList(
            childCommentsUiState.childComments,
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
            eventId = eventId,
        )
        binding.etChildcommentsEditchildcommentcontent.apply {
            text.clear()
        }
    }

    private fun onChildCommentDelete(commentId: Long) {
        viewModel.deleteComment(commentId, parentCommentId)
    }

    private fun onParentCommentDelete() {
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
            val intent = Intent(context, ChildCommentActivity::class.java).apply {
                putExtra(KEY_EVENT_ID, eventId)
                putExtra(KEY_PARENT_COMMENT_ID, parentCommentId)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            context.startActivity(intent)
        }
    }
}
