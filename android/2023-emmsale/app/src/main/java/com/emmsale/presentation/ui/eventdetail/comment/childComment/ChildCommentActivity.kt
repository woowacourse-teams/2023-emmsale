package com.emmsale.presentation.ui.eventdetail.comment.childComment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityChildCommentsBinding
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.ui.eventdetail.comment.childComment.recyclerView.ChildCommentAdapter
import com.emmsale.presentation.ui.eventdetail.comment.childComment.recyclerView.ChildCommentRecyclerViewDivider
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
    }

    private fun initChildCommentsRecyclerView() {
        binding.rvChildcommentsChildcomments.apply {
            adapter = ChildCommentAdapter(::onChildCommentDelete)
            itemAnimator = null
            addItemDecoration(ChildCommentRecyclerViewDivider(this@ChildCommentActivity))
        }
    }

    private fun setUpUiLogic() {
        viewModel.isLogin.observe(this) {
            handleNotLogin(it)
        }
        viewModel.comments.observe(this) {
            handleError(it)
            handleChildComments(it)
            handleEditComment()
            handleUpButton()
        }
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

        fun getIntent(context: Context, eventId: Long, parentCommentId: Long): Intent =
            Intent(context, ChildCommentActivity::class.java).apply {
                putExtra(KEY_EVENT_ID, eventId)
                putExtra(KEY_PARENT_COMMENT_ID, parentCommentId)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
    }
}
