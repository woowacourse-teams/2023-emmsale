package com.emmsale.presentation.ui.feedDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import com.emmsale.R
import com.emmsale.databinding.ActivityFeedDetailBinding
import com.emmsale.presentation.common.Event
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.views.InfoDialog
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.BottomMenuDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.MenuItemType
import com.emmsale.presentation.ui.childCommentList.ChildCommentActivity
import com.emmsale.presentation.ui.commentList.recyclerView.CommentRecyclerViewDivider
import com.emmsale.presentation.ui.feedDetail.recyclerView.CommentsAdapter
import com.emmsale.presentation.ui.feedDetail.recyclerView.FeedDetailAdapter
import com.emmsale.presentation.ui.feedDetail.uiState.FeedDetailUiEvent
import com.emmsale.presentation.ui.profile.ProfileActivity

class FeedDetailActivity : AppCompatActivity() {

    private val feedId: Long by lazy {
        intent.getLongExtra(KEY_FEED_ID, DEFAULT_FEED_ID)
    }

    private val binding: ActivityFeedDetailBinding by lazy {
        ActivityFeedDetailBinding.inflate(layoutInflater)
    }

    private val viewModel: FeedDetailViewModel by viewModels { FeedDetailViewModel.factory(feedId) }

    private val inputMethodManager: InputMethodManager by lazy {
        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private val feedDetailAdapter: FeedDetailAdapter = FeedDetailAdapter(::showProfile)

    private val commentsAdapter: CommentsAdapter = CommentsAdapter(
        onParentCommentClick = ::showChildComments,
        onProfileImageClick = ::showProfile,
        onCommentMenuClick = ::showCommentMenuDialog,
    )

    private val bottomMenuDialog: BottomMenuDialog by lazy { BottomMenuDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setUpDataBinding()
        setUpToolbar()
        setUpRecyclerView()
        setUpUiEvent()
        setUpCommentEditing()
        setUpFeedDetail()
    }

    private fun setUpDataBinding() {
        binding.lifecycleOwner = this
        binding.vm = viewModel
        binding.postComment = ::onCommentSave
        binding.cancelUpdateComment = ::cancelUpdateComment
        binding.updateComment = ::updateComment
    }

    private fun onCommentSave() {
        viewModel.saveComment(binding.etCommentsPostComment.text.toString())
        binding.etCommentsPostComment.text.clear()
        hideKeyboard()
    }

    private fun hideKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun showKeyboard() {
        @Suppress("DEPRECATION")
        inputMethodManager.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY,
        )
    }

    private fun cancelUpdateComment() {
        viewModel.setEditMode(false)
        hideKeyboard()
    }

    private fun updateComment() {
        val commentId = viewModel.editingCommentId.value ?: return
        val content = binding.etCommentsCommentUpdate.text.toString()
        viewModel.updateComment(commentId, content)
        hideKeyboard()
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
            binding.root.showSnackBar("아직 피드 수정 기능이 준비되지 않았습니다.")
        }
    }

    private fun BottomMenuDialog.addFeedDeleteButton() {
        addMenuItemBelow(context.getString(R.string.all_delete_button_label)) {
            viewModel.deleteFeed()
        }
    }

    private fun BottomMenuDialog.addFeedReportButton() {
        addMenuItemBelow(
            context.getString(R.string.all_report_button_label),
            MenuItemType.IMPORTANT,
        ) {
            binding.root.showSnackBar("아직 피드 신고 기능이 준비되지 않았습니다.")
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
            addItemDecoration(CommentRecyclerViewDivider(this@FeedDetailActivity))
        }
    }

    private fun showProfile(authorId: Long) {
        ProfileActivity.startActivity(this, authorId)
    }

    private fun showChildComments(commentId: Long) {
        ChildCommentActivity.startActivity(this, feedId, commentId)
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
            editComment(commentId)
        }
    }

    private fun BottomMenuDialog.addCommentDeleteButton(commentId: Long) {
        addMenuItemBelow(context.getString(R.string.all_delete_button_label)) {
            onCommentDeleteButtonClick(commentId)
        }
    }

    private fun onCommentDeleteButtonClick(commentId: Long) {
        val context = binding.root.context
        WarningDialog(
            context = context,
            title = context.getString(R.string.commentdeletedialog_title),
            message = context.getString(R.string.commentdeletedialog_message),
            positiveButtonLabel = context.getString(R.string.commentdeletedialog_positive_button_label),
            negativeButtonLabel = context.getString(R.string.commentdeletedialog_negative_button_label),
            onPositiveButtonClick = { deleteComment(commentId) },
        ).show()
    }

    private fun BottomMenuDialog.addCommentReportButton(commentId: Long) {
        addMenuItemBelow(
            context.getString(R.string.all_report_button_label),
            MenuItemType.IMPORTANT,
        ) { reportComment(commentId) }
    }

    private fun editComment(commentId: Long) {
        viewModel.setEditMode(true, commentId)
        binding.etCommentsCommentUpdate.requestFocus()
        showKeyboard()
    }

    private fun deleteComment(commentId: Long) {
        viewModel.deleteComment(commentId)
    }

    private fun reportComment(commentId: Long) {
        val context = this
        WarningDialog(
            context = context,
            title = context.getString(R.string.all_report_dialog_title),
            message = context.getString(R.string.comments_comment_report_dialog_message),
            positiveButtonLabel = context.getString(R.string.all_report_dialog_positive_button_label),
            negativeButtonLabel = context.getString(R.string.commentdeletedialog_negative_button_label),
            onPositiveButtonClick = {
                viewModel.reportComment(commentId)
            },
        ).show()
    }

    private fun setUpCommentEditing() {
        viewModel.editingCommentContent.observe(this) {
            if (it == null) return@observe
            binding.etCommentsCommentUpdate.setText(it)
        }
    }

    private fun setUpUiEvent() {
        viewModel.uiEvent.observe(this, ::handleUiEvent)
    }

    private fun handleUiEvent(event: Event<FeedDetailUiEvent>) {
        val content = event.getContentIfNotHandled() ?: return
        when (content) {
            FeedDetailUiEvent.None -> {}
            is FeedDetailUiEvent.UnexpectedError -> showToast(content.errorMessage)
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
        }
    }

    private fun setUpFeedDetail() {
        viewModel.feedDetail.observe(this) {
            feedDetailAdapter.setFeedDetail(it)
            commentsAdapter.submitList(it.comments)
        }
    }

    companion object {
        private const val KEY_FEED_ID: String = "KEY_FEED_ID"
        private const val DEFAULT_FEED_ID: Long = -1

        fun startActivity(context: Context, feedId: Long) {
            val intent = Intent(context, FeedDetailActivity::class.java)
            intent.putExtra(KEY_FEED_ID, feedId)
            context.startActivity(intent)
        }
    }
}
