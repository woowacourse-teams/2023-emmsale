package com.emmsale.presentation.ui.childCommentList

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.emmsale.R
import com.emmsale.data.model.Comment
import com.emmsale.databinding.ActivityChildCommentsBinding
import com.emmsale.presentation.base.NetworkActivity
import com.emmsale.presentation.common.extension.hideKeyboard
import com.emmsale.presentation.common.extension.showKeyboard
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.layoutManager.CenterSmoothScroller
import com.emmsale.presentation.common.layoutManager.EndSmoothScroller
import com.emmsale.presentation.common.recyclerView.DividerItemDecoration
import com.emmsale.presentation.common.views.InfoDialog
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.BottomMenuDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.MenuItemType
import com.emmsale.presentation.ui.childCommentList.ChildCommentsViewModel.Companion.KEY_FEED_ID
import com.emmsale.presentation.ui.childCommentList.ChildCommentsViewModel.Companion.KEY_PARENT_COMMENT_ID
import com.emmsale.presentation.ui.childCommentList.recyclerView.CommentsAdapter
import com.emmsale.presentation.ui.childCommentList.uiState.ChildCommentsUiEvent
import com.emmsale.presentation.ui.feedDetail.FeedDetailActivity
import com.emmsale.presentation.ui.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChildCommentsActivity :
    NetworkActivity<ActivityChildCommentsBinding>(R.layout.activity_child_comments) {

    override val viewModel: ChildCommentsViewModel by viewModels()

    @Inject
    lateinit var centerSmoothScroller: CenterSmoothScroller

    @Inject
    lateinit var endSmoothScroller: EndSmoothScroller

    private val commentsAdapter: CommentsAdapter = CommentsAdapter(
        onCommentClick = { },
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

    private fun showCommentMenuDialog(isWrittenByLoginUser: Boolean, comment: Comment) {
        bottomMenuDialog.resetMenu()
        if (isWrittenByLoginUser) {
            bottomMenuDialog.addCommentUpdateButton(comment.id)
            bottomMenuDialog.addCommentDeleteButton(comment.id)
        } else {
            bottomMenuDialog.addCommentReportButton(comment.id)
        }
        bottomMenuDialog.show()
    }

    private fun BottomMenuDialog.addCommentUpdateButton(commentId: Long) {
        addMenuItemBelow(context.getString(R.string.all_update_button_label)) {
            viewModel.startEditComment(commentId)
            binding.stiwCommentUpdate.requestFocusOnEditText()
            showKeyboard()
            startToEditComment(commentId)
        }
    }

    private fun startToEditComment(commentId: Long) {
        val position = getCommentPosition(commentId)

        lifecycleScope.launch {
            delay(KEYBOARD_SHOW_WAITING_TIME)
            binding.rvChildcommentsChildcomments
                .layoutManager
                ?.startSmoothScroll(endSmoothScroller.apply { targetPosition = position })
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
            viewModel.cancelEditComment()
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
            commentsAdapter.submitList(it.commentUiStates) { handleHighlightComment() }
        }
    }

    private fun handleHighlightComment() {
        if (highlightCommentId == INVALID_COMMENT_ID || isNotRealFirstEnter()) return

        viewModel.isAlreadyFirstFetched = true
        viewModel.highlightCommentOnFirstEnter(highlightCommentId)
        highlightCommentOnFirstEnter()
    }

    private fun isNotRealFirstEnter(): Boolean =
        viewModel.isAlreadyFirstFetched || viewModel.comments.value.commentUiStates.isEmpty()

    private fun highlightCommentOnFirstEnter() {
        val position = getCommentPosition(highlightCommentId)

        binding.rvChildcommentsChildcomments.scrollToPosition(position)
        lifecycleScope.launch {
            delay(100L) // 버그 때문에
            binding.rvChildcommentsChildcomments
                .layoutManager
                ?.startSmoothScroll(centerSmoothScroller.apply { targetPosition = position })
        }
    }

    private fun getCommentPosition(commentId: Long): Int = viewModel.comments.value.commentUiStates
        .indexOfFirst { commentUiState ->
            commentUiState.comment.id == commentId
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
        binding.rvChildcommentsChildcomments.smoothScrollToPosition(viewModel.comments.value.commentUiStates.size)
    }

    companion object {
        private const val KEY_HIGHLIGHT_COMMENT_ID = "KEY_HIGHLIGHT_COMMENT_ID"
        private const val KEY_FROM_POST_DETAIL = "KEY_FROM_POST_DETAIL"
        private const val INVALID_COMMENT_ID: Long = -1
        private const val KEYBOARD_SHOW_WAITING_TIME = 300L

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
