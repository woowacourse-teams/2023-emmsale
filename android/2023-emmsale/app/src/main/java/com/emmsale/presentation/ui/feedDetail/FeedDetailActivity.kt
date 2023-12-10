package com.emmsale.presentation.ui.feedDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearSmoothScroller
import com.emmsale.R
import com.emmsale.data.model.Comment
import com.emmsale.databinding.ActivityFeedDetailBinding
import com.emmsale.presentation.base.NetworkActivity
import com.emmsale.presentation.common.extension.hideKeyboard
import com.emmsale.presentation.common.extension.showKeyboard
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.recyclerView.DividerItemDecoration
import com.emmsale.presentation.common.views.InfoDialog
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.BottomMenuDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.MenuItemType
import com.emmsale.presentation.ui.childCommentList.ChildCommentsActivity
import com.emmsale.presentation.ui.feedDetail.FeedDetailViewModel.Companion.KEY_FEED_ID
import com.emmsale.presentation.ui.feedDetail.recyclerView.FeedAndCommentsAdapter
import com.emmsale.presentation.ui.feedDetail.uiState.FeedDetailUiEvent
import com.emmsale.presentation.ui.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedDetailActivity :
    NetworkActivity<ActivityFeedDetailBinding>(R.layout.activity_feed_detail) {

    private val highlightCommentId: Long by lazy {
        intent.getLongExtra(KEY_HIGHLIGHT_COMMENT_ID, INVALID_COMMENT_ID)
    }

    override val viewModel: FeedDetailViewModel by viewModels()

    private val bottomMenuDialog: BottomMenuDialog by lazy { BottomMenuDialog(this) }

    private val feedAndCommentsAdapter = FeedAndCommentsAdapter(
        onAuthorImageClick = ::navigateToProfile,
        onCommentClick = ::navigateToChildComments,
        onCommentMenuClick = ::showCommentMenuDialog,
    )

    private fun navigateToChildComments(comment: Comment) {
        ChildCommentsActivity.startActivity(
            context = this,
            feedId = comment.feed.id,
            parentCommentId = comment.parentCommentId ?: comment.id,
            highlightCommentId = comment.id,
        )
    }

    private fun navigateToProfile(authorId: Long) {
        ProfileActivity.startActivity(this, authorId)
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
            viewModel.setEditMode(true, commentId)
            binding.stiwCommentUpdate.requestFocusOnEditText()
            showKeyboard()
        }
    }

    private fun BottomMenuDialog.addCommentDeleteButton(commentId: Long) {
        addMenuItemBelow(context.getString(R.string.all_delete_button_label)) {
            showCommentDeleteDialog(commentId)
        }
    }

    private fun showCommentDeleteDialog(commentId: Long) {
        WarningDialog(
            context = this,
            title = getString(R.string.commentdeletedialog_title),
            message = getString(R.string.commentdeletedialog_message),
            positiveButtonLabel = getString(R.string.commentdeletedialog_positive_button_label),
            negativeButtonLabel = getString(R.string.commentdeletedialog_negative_button_label),
            onPositiveButtonClick = { viewModel.deleteComment(commentId) },
        ).show()
    }

    private fun BottomMenuDialog.addCommentReportButton(commentId: Long) {
        addMenuItemBelow(
            context.getString(R.string.all_report_button_label),
            MenuItemType.IMPORTANT,
        ) { showCommentReportConfirmDialog(commentId) }
    }

    private fun showCommentReportConfirmDialog(commentId: Long) {
        WarningDialog(
            context = this,
            title = getString(R.string.all_report_dialog_title),
            message = getString(R.string.comments_comment_report_dialog_message),
            positiveButtonLabel = getString(R.string.all_report_dialog_positive_button_label),
            negativeButtonLabel = getString(R.string.commentdeletedialog_negative_button_label),
            onPositiveButtonClick = {
                viewModel.reportComment(commentId)
            },
        ).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupDataBinding()
        setupToolbar()
        setupFeedAndCommentsRecyclerView()

        observeFeedDetail()
        observeUiEvent()
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.refresh()
    }

    private fun setupDataBinding() {
        binding.vm = viewModel
        binding.onCommentSubmitButtonClick = {
            viewModel.postComment(it)
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

    private fun setupToolbar() {
        binding.tbFeeddetailToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.tbFeeddetailToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.more -> showFeedDetailMenuDialog()
            }
            true
        }
    }

    private fun showFeedDetailMenuDialog() {
        bottomMenuDialog.resetMenu()
        if (viewModel.isFeedDetailWrittenByLoginUser) {
            bottomMenuDialog.addFeedUpdateButton()
            bottomMenuDialog.addFeedDeleteButton()
        } else {
            bottomMenuDialog.addFeedReportButton()
        }
        bottomMenuDialog.show()
    }

    private fun BottomMenuDialog.addFeedUpdateButton() {
        addMenuItemBelow(context.getString(R.string.all_update_button_label)) {
            binding.root.showSnackBar("아직 게시글 수정 기능이 준비되지 않았습니다.")
        }
    }

    private fun BottomMenuDialog.addFeedDeleteButton() {
        addMenuItemBelow(context.getString(R.string.all_delete_button_label)) {
            showFeedDeleteConfirmDialog()
        }
    }

    private fun showFeedDeleteConfirmDialog() {
        WarningDialog(
            context = this,
            title = getString(R.string.feeddetaildeletedialog_title),
            message = getString(R.string.feeddetaildeletedialog_message),
            positiveButtonLabel = getString(R.string.all_delete_button_label),
            negativeButtonLabel = getString(R.string.all_cancel),
            onPositiveButtonClick = { viewModel.deleteFeed() },
        ).show()
    }

    private fun BottomMenuDialog.addFeedReportButton() {
        addMenuItemBelow(
            context.getString(R.string.all_report_button_label),
            MenuItemType.IMPORTANT,
        ) {
            binding.root.showSnackBar("아직 게시글 신고 기능이 준비되지 않았습니다.")
        }
    }

    private fun setupFeedAndCommentsRecyclerView() {
        binding.rvFeedAndComments.apply {
            adapter = feedAndCommentsAdapter
            itemAnimator = null
            addItemDecoration(DividerItemDecoration(this@FeedDetailActivity))
        }
    }

    private fun observeFeedDetail() {
        viewModel.feedDetailUiState.observe(this) {
            val feedAndComments = listOf(it.feedUiState) + it.commentsUiState.commentUiStates
            feedAndCommentsAdapter.submitList(feedAndComments) {
                if (highlightCommentId == INVALID_COMMENT_ID || isNotRealFirstFetch()) return@submitList
                viewModel.highlightComment(highlightCommentId)
                viewModel.isAlreadyFirstFetched = true
            }
        }
    }

    private fun isNotRealFirstFetch(): Boolean =
        viewModel.isAlreadyFirstFetched || viewModel.commentUiStates.isEmpty()

    private fun highlightComment(commentId: Long) {
        val position = viewModel.commentUiStates
            .indexOfFirst {
                it.comment.id == commentId
            }

        binding.rvFeedAndComments.scrollToPosition(position + 1)
        lifecycleScope.launch {
            delay(200L)
            binding.rvFeedAndComments.layoutManager?.startSmoothScroll(
                object : LinearSmoothScroller(this@FeedDetailActivity) {
                    override fun getVerticalSnapPreference(): Int = SNAP_TO_START
                }.apply { targetPosition = position + 1 },
            )
        }
    }

    private fun observeUiEvent() {
        viewModel.uiEvent.observe(this, ::handleUiEvent)
    }

    private fun handleUiEvent(uiEvent: FeedDetailUiEvent) {
        when (uiEvent) {
            FeedDetailUiEvent.CommentDeleteFail -> binding.root.showSnackBar(getString(R.string.comments_comments_delete_error_message))
            FeedDetailUiEvent.CommentPostFail -> binding.root.showSnackBar(getString(R.string.comments_comments_posting_error_message))
            FeedDetailUiEvent.CommentReportComplete -> InfoDialog(
                context = this,
                title = getString(R.string.all_report_complete_dialog_title),
                message = getString(R.string.all_report_complete_dialog_message),
                buttonLabel = getString(R.string.all_okay),
            ).show()

            FeedDetailUiEvent.CommentReportDuplicate -> InfoDialog(
                context = this,
                title = getString(R.string.all_report_duplicate_dialog_title),
                message = getString(R.string.all_report_duplicate_message),
                buttonLabel = getString(R.string.all_okay),
            ).show()

            FeedDetailUiEvent.CommentReportFail -> binding.root.showSnackBar(R.string.all_report_fail_message)
            FeedDetailUiEvent.CommentUpdateFail -> binding.root.showSnackBar(getString(R.string.comments_comments_update_error_message))
            FeedDetailUiEvent.FeedDeleteComplete -> {
                InfoDialog(
                    context = this,
                    title = getString(R.string.feeddetail_feed_delete_complete_title),
                    message = getString(R.string.feeddetail_feed_delete_complete_message),
                    buttonLabel = getString(R.string.all_okay),
                    onButtonClick = { finish() },
                    cancelable = false,
                ).show()
            }

            FeedDetailUiEvent.FeedDeleteFail -> binding.root.showSnackBar(getString(R.string.feeddetail_feed_delete_fail_message))
            FeedDetailUiEvent.DeletedFeedFetch -> {
                InfoDialog(
                    context = this,
                    title = getString(R.string.feeddetail_deleted_feed_fetch_title),
                    message = getString(R.string.feeddetail_deleted_feed_fetch_message),
                    buttonLabel = getString(R.string.all_okay),
                    onButtonClick = { finish() },
                    cancelable = false,
                ).show()
            }

            FeedDetailUiEvent.CommentPostComplete -> {
                binding.btiwCommentPost.clearText()
                scrollToLastPosition()
            }

            is FeedDetailUiEvent.CommentHighlight -> highlightComment(uiEvent.commentId)
        }
    }

    private fun scrollToLastPosition() {
        val commentsCount = viewModel.commentUiStates.size
        binding.rvFeedAndComments.smoothScrollToPosition(commentsCount)
    }

    companion object {
        private const val KEY_HIGHLIGHT_COMMENT_ID = "KEY_HIGHLIGHT_COMMENT_ID"
        private const val INVALID_COMMENT_ID: Long = -1

        fun startActivity(
            context: Context,
            feedId: Long,
            highlightCommentId: Long = INVALID_COMMENT_ID,
        ) {
            context.startActivity(getIntent(context, feedId, highlightCommentId))
        }

        fun getIntent(
            context: Context,
            feedId: Long,
            highlightCommentId: Long = INVALID_COMMENT_ID,
        ) = Intent(context, FeedDetailActivity::class.java)
            .putExtra(KEY_FEED_ID, feedId)
            .putExtra(KEY_HIGHLIGHT_COMMENT_ID, highlightCommentId)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }
}
