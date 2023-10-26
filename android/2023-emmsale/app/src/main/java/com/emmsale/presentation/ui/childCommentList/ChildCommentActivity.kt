package com.emmsale.presentation.ui.childCommentList

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityChildCommentsBinding
import com.emmsale.presentation.common.Event
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.recyclerView.CommonRecyclerViewDivider
import com.emmsale.presentation.common.views.InfoDialog
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.BottomMenuDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.MenuItemType
import com.emmsale.presentation.ui.childCommentList.ChildCommentViewModel.Companion.KEY_FEED_ID
import com.emmsale.presentation.ui.childCommentList.ChildCommentViewModel.Companion.KEY_PARENT_COMMENT_ID
import com.emmsale.presentation.ui.childCommentList.uiState.ChildCommentsUiEvent
import com.emmsale.presentation.ui.childCommentList.uiState.ChildCommentsUiState
import com.emmsale.presentation.ui.feedDetail.FeedDetailActivity
import com.emmsale.presentation.ui.feedDetail.recyclerView.CommentsAdapter
import com.emmsale.presentation.ui.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChildCommentActivity : AppCompatActivity() {
    private val binding by lazy { ActivityChildCommentsBinding.inflate(layoutInflater) }
    private val viewModel: ChildCommentViewModel by viewModels()

    private val inputMethodManager: InputMethodManager by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private val commentsAdapter: CommentsAdapter = CommentsAdapter(
        onParentCommentClick = {},
        onProfileImageClick = ::showProfile,
        onCommentMenuClick = ::showCommentMenuDialog,
    )

    private val bottomMenuDialog: BottomMenuDialog by lazy { BottomMenuDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initDataBinding()
        initBackPressedDispatcher()
        initToolbar()
        initChildCommentsRecyclerView()
        setupUiLogic()
    }

    override fun onStart() {
        super.onStart()
        viewModel.refresh()
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

    private fun showProfile(authorId: Long) {
        ProfileActivity.startActivity(this, authorId)
    }

    private fun editComment(commentId: Long) {
        viewModel.setEditMode(true, commentId)
        binding.etChildcommentsCommentUpdate.requestFocus()
        showKeyboard()
    }

    private fun deleteComment(commentId: Long) {
        viewModel.deleteComment(commentId)
    }

    private fun reportComment(commentId: Long) {
        WarningDialog(
            context = this,
            title = getString(R.string.all_report_dialog_title),
            message = getString(R.string.comments_comment_report_dialog_message),
            positiveButtonLabel = getString(R.string.all_report_dialog_positive_button_label),
            negativeButtonLabel = getString(R.string.commentdeletedialog_negative_button_label),
            onPositiveButtonClick = { viewModel.reportComment(commentId) },
        ).show()
    }

    private fun initDataBinding() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.onCommentSubmitButtonClick = ::saveChildComment
        binding.cancelUpdateComment = ::cancelUpdateComment
        binding.updateComment = ::updateComment
    }

    private fun cancelUpdateComment() {
        viewModel.setEditMode(false)
        hideKeyboard()
    }

    private fun updateComment() {
        val commentId = viewModel.editingCommentId.value ?: return
        val content = binding.etChildcommentsCommentUpdate.text.toString()
        viewModel.updateComment(commentId, content)
        hideKeyboard()
    }

    private fun initBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(this, ChildCommentOnBackPressedCallback())
    }

    private fun initToolbar() {
        binding.tbChildcommentsToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun initChildCommentsRecyclerView() {
        binding.rvChildcommentsChildcomments.apply {
            adapter = commentsAdapter
            itemAnimator = null
            addItemDecoration(CommonRecyclerViewDivider(this@ChildCommentActivity))
        }
    }

    private fun setupUiLogic() {
        setupCommentsUiLogic()
        setupEditingCommentUiLogic()
        setUpUiEvent()
    }

    private fun setupCommentsUiLogic() {
        viewModel.comments.observe(this) {
            handleChildComments(it)
        }
    }

    private fun setupEditingCommentUiLogic() {
        viewModel.editingCommentContent.observe(this) {
            if (it == null) return@observe
            binding.etChildcommentsCommentUpdate.setText(it)
        }
    }

    private fun handleChildComments(comments: ChildCommentsUiState) {
        commentsAdapter.submitList(comments.comments)
    }

    private fun setUpUiEvent() {
        viewModel.uiEvent.observe(this) {
            handleUiEvent(it)
        }
    }

    private fun handleUiEvent(event: Event<ChildCommentsUiEvent>) {
        val content = event.getContentIfNotHandled() ?: return
        when (content) {
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
            ChildCommentsUiEvent.None -> {}
            is ChildCommentsUiEvent.UnexpectedError -> showToast(content.errorMessage)
            ChildCommentsUiEvent.CommentPostComplete -> scrollToLastPosition()
        }
    }

    private fun saveChildComment(content: String) {
        viewModel.saveChildComment(content)
        hideKeyboard()
    }

    private fun scrollToLastPosition() {
        binding.rvChildcommentsChildcomments.smoothScrollToPosition(viewModel.comments.value.comments.size)
    }

    private fun hideKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun showKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        @Suppress("DEPRECATION")
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    companion object {
        private const val KEY_FROM_NOTIFICATION = "KEY_FROM_NOTIFICATION"

        fun startActivity(context: Context, feedId: Long, parentCommentId: Long) {
            val intent = Intent(context, ChildCommentActivity::class.java).apply {
                putExtra(KEY_FEED_ID, feedId)
                putExtra(KEY_PARENT_COMMENT_ID, parentCommentId)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            context.startActivity(intent)
        }

        fun getIntent(
            context: Context,
            feedId: Long,
            parentCommentId: Long,
            fromNotification: Boolean = false,
        ): Intent =
            Intent(context, ChildCommentActivity::class.java).apply {
                putExtra(KEY_FEED_ID, feedId)
                putExtra(KEY_PARENT_COMMENT_ID, parentCommentId)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                if (fromNotification) putExtra(KEY_FROM_NOTIFICATION, true)
            }
    }

    inner class ChildCommentOnBackPressedCallback : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (intent.getBooleanExtra(KEY_FROM_NOTIFICATION, false)) {
                FeedDetailActivity.startActivity(this@ChildCommentActivity, viewModel.feedId)
            }
            finish()
        }
    }
}
