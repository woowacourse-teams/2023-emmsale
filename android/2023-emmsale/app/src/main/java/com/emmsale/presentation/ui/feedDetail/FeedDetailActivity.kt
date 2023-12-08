package com.emmsale.presentation.ui.feedDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.emmsale.R
import com.emmsale.databinding.ActivityFeedDetailBinding
import com.emmsale.presentation.base.NetworkActivity
import com.emmsale.presentation.common.UiEvent
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
import com.emmsale.presentation.ui.feedDetail.recyclerView.CommentsAdapter
import com.emmsale.presentation.ui.feedDetail.recyclerView.FeedDetailAdapter
import com.emmsale.presentation.ui.feedDetail.uiState.FeedDetailUiEvent
import com.emmsale.presentation.ui.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedDetailActivity :
    NetworkActivity<ActivityFeedDetailBinding>(R.layout.activity_feed_detail) {

    override val viewModel: FeedDetailViewModel by viewModels()

    private val bottomMenuDialog: BottomMenuDialog by lazy { BottomMenuDialog(this) }

    private val feedDetailAdapter: FeedDetailAdapter = FeedDetailAdapter(::showProfile)
    private val commentsAdapter: CommentsAdapter = CommentsAdapter(
        onCommentClick = { comment ->
            ChildCommentsActivity.startActivity(
                context = this,
                feedId = comment.feed.id,
                parentCommentId = comment.parentCommentId ?: comment.id,
                highlightCommentId = comment.id,
            )
        },
        onAuthorImageClick = ::showProfile,
        onCommentMenuClick = ::showCommentMenuDialog,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setUpDataBinding()
        setUpToolbar()
        setUpRecyclerView()
        setUpUiEvent()
        setUpCommentEditing()
        setUpFeed()
        setupComments()
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.refresh()
    }

    private fun setUpDataBinding() {
        binding.lifecycleOwner = this
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

    private fun setUpToolbar() {
        binding.tbFeeddetailToolbar.setNavigationOnClickListener { finish() }
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

    private fun setUpRecyclerView() {
        val concatAdapterConfig = ConcatAdapter.Config.Builder().build()

        binding.rvFeeddetailFeedAndComments.apply {
            adapter = ConcatAdapter(
                concatAdapterConfig,
                feedDetailAdapter,
                commentsAdapter,
            )
            itemAnimator = null
            addItemDecoration(DividerItemDecoration(this@FeedDetailActivity))
        }
    }

    private fun showProfile(authorId: Long) {
        ProfileActivity.startActivity(this, authorId)
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

    private fun setUpCommentEditing() {
        viewModel.editingCommentContent.observe(this) {
            if (it == null) return@observe
        }
    }

    private fun setUpUiEvent() {
        viewModel.uiEvent.observe(this, ::handleUiEvent)
    }

    private fun handleUiEvent(event: UiEvent<FeedDetailUiEvent>) {
        val content = event.getContentIfNotHandled() ?: return
        when (content) {
            FeedDetailUiEvent.None -> {}
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
                ).show()
                finish()
            }

            FeedDetailUiEvent.FeedDeleteFail -> binding.root.showSnackBar(getString(R.string.feeddetail_feed_delete_fail_message))
            FeedDetailUiEvent.DeletedFeedFetch -> {
                InfoDialog(
                    context = this,
                    title = getString(R.string.feeddetail_deleted_feed_fetch_title),
                    message = getString(R.string.feeddetail_deleted_feed_fetch_message),
                    buttonLabel = getString(R.string.all_okay),
                ).show()
                finish()
            }

            FeedDetailUiEvent.CommentPostComplete -> {
                binding.btiwCommentPost.clearText()
                scrollToLastPosition()
            }
        }
    }

    private fun scrollToLastPosition() {
        binding.rvFeeddetailFeedAndComments.smoothScrollToPosition(viewModel.comments.value.size + 1)
    }

    private fun setUpFeed() {
        viewModel.feed.observe(this) {
            feedDetailAdapter.setFeedDetail(it)
        }
    }

    private fun setupComments() {
        viewModel.comments.observe(this) {
            commentsAdapter.submitList(it.comments)
        }
    }

    companion object {
        fun startActivity(
            context: Context,
            feedId: Long,
        ) {
            context.startActivity(getIntent(context, feedId))
        }

        fun getIntent(
            context: Context,
            feedId: Long,
        ) = Intent(context, FeedDetailActivity::class.java)
            .putExtra(KEY_FEED_ID, feedId)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }
}
