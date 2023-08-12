package com.emmsale.presentation.ui.eventdetail.comment.childComment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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
        initToolbar()
        initChildCommentsRecyclerView()
        setupUiLogic()

        viewModel.fetchComment(parentCommentId)
    }

    private fun initDataBinding() {
        binding.viewModel = viewModel
    }

    private fun initToolbar() {
        binding.tbChildcommentsToolbar.setNavigationOnClickListener { finish() }
    }

    private fun initChildCommentsRecyclerView() {
        binding.rvChildcommentsChildcomments.apply {
            adapter = ChildCommentAdapter(::onChildCommentDelete)
            itemAnimator = null
            addItemDecoration(ChildCommentRecyclerViewDivider(this@ChildCommentActivity))
        }
    }

    private fun setupUiLogic() {
        viewModel.isLogin.observe(this) {
            handleNotLogin(it)
        }
        viewModel.comments.observe(this) {
            handleError(it)
            handleChildComments(it)
            handleEditComment()
            handleProgressBar(it)
        }
    }

    private fun handleError(childComments: ChildCommentsUiState) {
        fun handleCommentsFetchingError(childCommentsUiState: ChildCommentsUiState) {
            if (childCommentsUiState.isFetchingError) {
                showToast(getString(R.string.comments_comments_fetching_error_message))
            }
        }

        fun handleCommentPostingError(childComments: ChildCommentsUiState) {
            if (childComments.isPostingError) {
                showToast(getString(R.string.comments_comments_posting_error_message))
            }
        }

        fun handleCommentDeletionError(childComments: ChildCommentsUiState) {
            if (childComments.isDeletionError) {
                showToast(getString(R.string.comments_comments_deletion_error_message))
            }
        }
        handleCommentsFetchingError(childComments)
        handleCommentPostingError(childComments)
        handleCommentDeletionError(childComments)
    }

    private fun handleNotLogin(isLogin: Boolean) {
        if (!isLogin) {
            LoginActivity.startActivity(this)
            finish()
        }
    }

    private fun handleChildComments(childComments: ChildCommentsUiState) {
        (binding.rvChildcommentsChildcomments.adapter as ChildCommentAdapter).submitList(
            listOf(childComments.parentComment) + childComments.childComments,
        )
    }

    private fun handleEditComment() {
        binding.tvChildcommentsPostchildcommentbutton.setOnClickListener {
            onChildCommentSave()
        }
    }

    private fun handleProgressBar(childComments: ChildCommentsUiState) {
        binding.progressBar.isVisible = childComments.isLoading
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
