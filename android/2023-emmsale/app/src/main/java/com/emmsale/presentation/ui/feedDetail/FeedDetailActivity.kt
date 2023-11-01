package com.emmsale.presentation.ui.feedDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R
import com.emmsale.databinding.ActivityFeedDetailBinding
import com.emmsale.presentation.common.Event
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.recyclerView.DividerItemDecoration
import com.emmsale.presentation.common.views.InfoDialog
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.BottomMenuDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.MenuItemType
import com.emmsale.presentation.ui.childCommentList.ChildCommentActivity
import com.emmsale.presentation.ui.feedDetail.FeedDetailViewModel.Companion.KEY_FEED_ID
import com.emmsale.presentation.ui.feedDetail.recyclerView.CommentsAdapter
import com.emmsale.presentation.ui.feedDetail.recyclerView.FeedDetailAdapter
import com.emmsale.presentation.ui.feedDetail.uiState.FeedDetailUiEvent
import com.emmsale.presentation.ui.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class FeedDetailActivity : AppCompatActivity() {
    private val binding by lazy { ActivityFeedDetailBinding.inflate(layoutInflater) }

    private val viewModel: FeedDetailViewModel by viewModels()

    private val highlightCommentId: Long by lazy {
        intent.getLongExtra(KEY_HIGHLIGHT_COMMENT_ID, INVALID_COMMENT_ID)
    }

    private var justEntered: Boolean by Delegates.vetoable(true) { _, oldValue, newValue ->
        oldValue && !newValue
    }

    private val inputMethodManager: InputMethodManager by lazy {
        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    }
    private val bottomMenuDialog: BottomMenuDialog by lazy { BottomMenuDialog(this) }

    private val feedDetailAdapter: FeedDetailAdapter = FeedDetailAdapter(::showProfile)
    private val commentsAdapter: CommentsAdapter = CommentsAdapter(
        onClick = { comment ->
            ChildCommentActivity.startActivity(
                context = this,
                feedId = comment.feedId,
                parentCommentId = comment.parentId ?: comment.id,
                highlightCommentId = comment.id,
            )
        },
        onAuthorImageClick = ::showProfile,
        onCommentMenuClick = ::showCommentMenuDialog,
    ).apply {
        registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (highlightCommentId == INVALID_COMMENT_ID || !justEntered || itemCount == 0) return
                val position = viewModel.feedDetail.value.comments
                    .indexOfFirst { it.comment.id == highlightCommentId } + FEED_DETAIL_COUNT
                binding.rvFeeddetailFeedAndComments.scrollToPosition(position)

                viewModel.highlightComment(highlightCommentId)

                justEntered = false
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (savedInstanceState != null) justEntered = false

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
            binding.root.showSnackBar("아직 게시글 수정 기능이 준비되지 않았습니다.")
        }
    }

    private fun BottomMenuDialog.addFeedDeleteButton() {
        addMenuItemBelow(context.getString(R.string.all_delete_button_label)) {
            onFeedDeleteButtonClick()
        }
    }

    private fun onFeedDeleteButtonClick() {
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
            editComment(commentId)
        }
    }

    private fun BottomMenuDialog.addCommentDeleteButton(commentId: Long) {
        addMenuItemBelow(context.getString(R.string.all_delete_button_label)) {
            onCommentDeleteButtonClick(commentId)
        }
    }

    private fun onCommentDeleteButtonClick(commentId: Long) {
        WarningDialog(
            context = this,
            title = getString(R.string.commentdeletedialog_title),
            message = getString(R.string.commentdeletedialog_message),
            positiveButtonLabel = getString(R.string.commentdeletedialog_positive_button_label),
            negativeButtonLabel = getString(R.string.commentdeletedialog_negative_button_label),
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

            FeedDetailUiEvent.CommentPostComplete -> scrollToLastPosition()
        }
    }

    private fun scrollToLastPosition() {
        binding.rvFeeddetailFeedAndComments.smoothScrollToPosition(viewModel.feedDetail.value.comments.size + 1)
    }

    private fun setUpFeedDetail() {
        viewModel.feedDetail.observe(this) {
            feedDetailAdapter.setFeedDetail(it)
            commentsAdapter.submitList(it.comments)
        }
    }

    companion object {
        private const val KEY_HIGHLIGHT_COMMENT_ID = "KEY_HIGHLIGHT_COMMENT_ID"
        private const val INVALID_COMMENT_ID: Long = -1
        private const val FEED_DETAIL_COUNT: Int = 1

        fun startActivity(
            context: Context,
            feedId: Long,
            highlightCommentId: Long = INVALID_COMMENT_ID,
        ) {
            val intent =
                getIntent(context, feedId, highlightCommentId)
            context.startActivity(intent)
        }

        fun getIntent(
            context: Context,
            feedId: Long,
            highlightCommentId: Long = INVALID_COMMENT_ID,
        ): Intent =
            Intent(context, FeedDetailActivity::class.java).apply {
                putExtra(KEY_FEED_ID, feedId)
                putExtra(KEY_HIGHLIGHT_COMMENT_ID, highlightCommentId)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
    }
}
