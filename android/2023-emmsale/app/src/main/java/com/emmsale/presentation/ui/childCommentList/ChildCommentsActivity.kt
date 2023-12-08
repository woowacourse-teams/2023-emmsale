package com.emmsale.presentation.ui.childCommentList

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.emmsale.R
import com.emmsale.databinding.ActivityChildCommentsBinding
import com.emmsale.presentation.base.NetworkActivity
import com.emmsale.presentation.common.extension.hideKeyboard
import com.emmsale.presentation.common.extension.showKeyboard
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.recyclerView.DividerItemDecoration
import com.emmsale.presentation.common.views.InfoDialog
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.BottomMenuDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.MenuItemType
import com.emmsale.presentation.ui.childCommentList.ChildCommentsViewModel.Companion.KEY_FEED_ID
import com.emmsale.presentation.ui.childCommentList.ChildCommentsViewModel.Companion.KEY_PARENT_COMMENT_ID
import com.emmsale.presentation.ui.childCommentList.uiState.ChildCommentsUiEvent
import com.emmsale.presentation.ui.feedDetail.FeedDetailActivity
import com.emmsale.presentation.ui.feedDetail.recyclerView.CommentsAdapter
import com.emmsale.presentation.ui.feedDetail.uiState.CommentsUiState
import com.emmsale.presentation.ui.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChildCommentsActivity :
    NetworkActivity<ActivityChildCommentsBinding>(R.layout.activity_child_comments) {

    override val viewModel: ChildCommentsViewModel by viewModels()

    private val commentsAdapter: CommentsAdapter = CommentsAdapter(
        onCommentClick = { comment -> viewModel.unhighlight(comment.id) },
        onAuthorImageClick = { authorId -> ProfileActivity.startActivity(this, authorId) },
        onCommentMenuClick = ::showCommentMenuDialog,
    )

    private val bottomMenuDialog: BottomMenuDialog by lazy { BottomMenuDialog(this) }

    private val highlightCommentId: Long by lazy {
        intent.getLongExtra(KEY_HIGHLIGHT_COMMENT_ID, INVALID_COMMENT_ID)
    }

    private val fromPostDetail: Boolean by lazy {
        intent.getBooleanExtra(KEY_FROM_POST_DETAIL, true)
    }

    private fun showCommentMenuDialog(isWrittenByLoginUser: Boolean, commentId: Long) {
        bottomMenuDialog.resetMenu()
        if (isWrittenByLoginUser) {
            bottomMenuDialog.addCommentUpdateButton(commentId)
            bottomMenuDialog.addCommentDeleteButton(commentId)
        } else {
            bottomMenuDialog.addCommentReportButton(commentId)
        }
        bottomMenuDialog.show()
    }

    private fun BottomMenuDialog.addCommentUpdateButton(commentId: Long) {
        addMenuItemBelow(context.getString(R.string.all_update_button_label)) {
            viewModel.setEditMode(true, commentId)
            binding.stiwCommentUpdate.requestFocusOnEditText()
            showKeyboard()
        }
    }

    private fun BottomMenuDialog.addCommentDeleteButton(commentId: Long) {
        addMenuItemBelow(context.getString(R.string.all_delete_button_label)) {
            showDeleteCommentConfirmDialog(commentId)
        }
    }

    private fun showDeleteCommentConfirmDialog(commentId: Long) {
        val context = binding.root.context
        WarningDialog(
            context = context,
            title = context.getString(R.string.commentdeletedialog_title),
            message = context.getString(R.string.commentdeletedialog_message),
            positiveButtonLabel = context.getString(R.string.commentdeletedialog_positive_button_label),
            negativeButtonLabel = context.getString(R.string.commentdeletedialog_negative_button_label),
            onPositiveButtonClick = { viewModel.deleteComment(commentId) },
        ).show()
    }

    private fun BottomMenuDialog.addCommentReportButton(commentId: Long) {
        addMenuItemBelow(
            context.getString(R.string.all_report_button_label),
            MenuItemType.IMPORTANT,
        ) { showReportConfirmDialog(commentId) }
    }

    private fun showReportConfirmDialog(commentId: Long) {
        WarningDialog(
            context = this,
            title = getString(R.string.all_report_dialog_title),
            message = getString(R.string.comments_comment_report_dialog_message),
            positiveButtonLabel = getString(R.string.all_report_dialog_positive_button_label),
            negativeButtonLabel = getString(R.string.commentdeletedialog_negative_button_label),
            onPositiveButtonClick = { viewModel.reportComment(commentId) },
        ).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupDataBinding()
        setupBackPressedDispatcher()
        setupToolbar()
        setupChildCommentsRecyclerView()

        observeComments()
        observeUiEvent()
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.refresh()
    }

    private fun setupDataBinding() {
        binding.viewModel = viewModel
        binding.onCommentSubmitButtonClick = {
            viewModel.postChildComment(it)
            hideKeyboard()
        }
        binding.onCommentUpdateCancelButtonClick = {
            viewModel.setEditMode(false)
            hideKeyboard()
        }
        binding.onUpdatedCommentSubmitButtonClick = {
            val commentId = viewModel.editingCommentId.value
            if (commentId != null) viewModel.updateComment(commentId, it)
            hideKeyboard()
        }
    }

    private fun setupBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!fromPostDetail) {
                        FeedDetailActivity.startActivity(
                            this@ChildCommentsActivity,
                            viewModel.feedId,
                        )
                    }
                    finish()
                }
            },
        )
    }

    private fun setupToolbar() {
        binding.tbChildcommentsToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupChildCommentsRecyclerView() {
        binding.rvChildcommentsChildcomments.apply {
            adapter = commentsAdapter
            itemAnimator = null
            addItemDecoration(DividerItemDecoration(this@ChildCommentsActivity))
        }
    }

    private fun observeComments() {
        viewModel.comments.observe(this) {
            commentsAdapter.submitList(it.comments) { scrollToIfFirstFetch(it) }
        }
    }

    private fun scrollToIfFirstFetch(commentUiState: CommentsUiState) {
        fun cantScroll(): Boolean =
            viewModel.isAlreadyFirstFetched || commentUiState.comments.isEmpty()

        if (highlightCommentId == INVALID_COMMENT_ID || cantScroll()) return
        val position = viewModel.comments.value.comments
            .indexOfFirst {
                it.comment.id == highlightCommentId
            }
        binding.rvChildcommentsChildcomments.scrollToPosition(position)

        viewModel.highlight(highlightCommentId)
        viewModel.isAlreadyFirstFetched = true
    }

    private fun observeUiEvent() {
        viewModel.uiEvent.observe(this, ::handleUiEvent)
    }

    private fun handleUiEvent(event: ChildCommentsUiEvent) {
        when (event) {
            ChildCommentsUiEvent.CommentReportFail -> binding.root.showSnackBar(getString(R.string.all_report_fail_message))
            ChildCommentsUiEvent.CommentReportComplete -> InfoDialog(
                context = this,
                title = getString(R.string.all_report_complete_dialog_title),
                message = getString(R.string.all_report_complete_dialog_message),
                buttonLabel = getString(R.string.all_okay),
            ).show()

            ChildCommentsUiEvent.CommentReportDuplicate -> InfoDialog(
                context = this,
                title = getString(R.string.all_report_duplicate_dialog_title),
                message = getString(R.string.all_report_duplicate_message),
                buttonLabel = getString(R.string.all_okay),
            ).show()

            ChildCommentsUiEvent.CommentPostFail -> binding.root.showSnackBar(getString(R.string.comments_comments_posting_error_message))
            ChildCommentsUiEvent.CommentUpdateFail -> binding.root.showSnackBar(getString(R.string.comments_comments_update_error_message))
            ChildCommentsUiEvent.CommentDeleteFail -> binding.root.showSnackBar(getString(R.string.comments_comments_delete_error_message))
            ChildCommentsUiEvent.CommentPostComplete -> {
                smoothScrollToLastPosition()
                binding.btiwCommentPost.clearText()
            }

            ChildCommentsUiEvent.CommentUpdateComplete ->
                binding.stiwCommentUpdate.isVisible = false

            ChildCommentsUiEvent.IllegalCommentFetch -> InfoDialog(
                context = this,
                title = getString(R.string.all_fetch_fail_title),
                message = getString(R.string.comments_not_exist_comment_message),
                buttonLabel = getString(R.string.all_okay),
                onButtonClick = { finish() },
                cancelable = false,
            ).show()
        }
    }

    private fun smoothScrollToLastPosition() {
        binding.rvChildcommentsChildcomments.smoothScrollToPosition(viewModel.comments.value.comments.size)
    }

    companion object {
        private const val KEY_HIGHLIGHT_COMMENT_ID = "KEY_HIGHLIGHT_COMMENT_ID"
        private const val KEY_FROM_POST_DETAIL = "KEY_FROM_POST_DETAIL"
        private const val INVALID_COMMENT_ID: Long = -1

        fun startActivity(
            context: Context,
            feedId: Long,
            parentCommentId: Long,
            highlightCommentId: Long = INVALID_COMMENT_ID,
            fromPostDetail: Boolean = true,
        ) {
            val intent = getIntent(
                context = context,
                feedId = feedId,
                parentCommentId = parentCommentId,
                highlightCommentId = highlightCommentId,
                fromPostDetail = fromPostDetail,
            )
            context.startActivity(intent)
        }

        fun getIntent(
            context: Context,
            feedId: Long,
            parentCommentId: Long,
            highlightCommentId: Long = INVALID_COMMENT_ID,
            fromPostDetail: Boolean = true,
        ) = Intent(context, ChildCommentsActivity::class.java)
            .putExtra(KEY_FEED_ID, feedId)
            .putExtra(KEY_PARENT_COMMENT_ID, parentCommentId)
            .putExtra(KEY_HIGHLIGHT_COMMENT_ID, highlightCommentId)
            .putExtra(KEY_FROM_POST_DETAIL, fromPostDetail)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }
}
