package com.emmsale.presentation.ui.eventdetail.comment.childComment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityChildCommentsBinding
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.views.InfoDialog
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.ui.eventdetail.comment.childComment.recyclerView.ChildCommentAdapter
import com.emmsale.presentation.ui.eventdetail.comment.childComment.recyclerView.ChildCommentRecyclerViewDivider
import com.emmsale.presentation.ui.eventdetail.comment.childComment.uiState.ChildCommentsEvent
import com.emmsale.presentation.ui.eventdetail.comment.childComment.uiState.ChildCommentsUiState
import com.emmsale.presentation.ui.login.LoginActivity
import com.emmsale.presentation.ui.profile.ProfileActivity

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
        binding.lifecycleOwner = this
        binding.cancelUpdateComment = ::cancelUpdateComment
        binding.updateComment = ::updateComment
    }

    private fun cancelUpdateComment() {
        viewModel.setEditMode(false)
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etChildcommentsCommentUpdate.windowToken, 0)
    }

    private fun updateComment() {
        val commentId = viewModel.editingCommentId.value ?: return
        val content = binding.etChildcommentsCommentUpdate.text.toString()
        viewModel.updateComment(commentId, content, parentCommentId)
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etChildcommentsCommentUpdate.windowToken, 0)
    }

    private fun initToolbar() {
        binding.tbChildcommentsToolbar.setNavigationOnClickListener { finish() }
    }

    private fun initChildCommentsRecyclerView() {
        binding.rvChildcommentsChildcomments.apply {
            adapter =
                ChildCommentAdapter(::showProfile, ::editComment, ::deleteComment, ::reportComment)
            itemAnimator = null
            addItemDecoration(ChildCommentRecyclerViewDivider(this@ChildCommentActivity))
        }
    }

    private fun showProfile(authorId: Long) {
        ProfileActivity.startActivity(this, authorId)
    }

    private fun editComment(commentId: Long) {
        viewModel.setEditMode(true, commentId)
        binding.etChildcommentsCommentUpdate.requestFocus()

        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        @Suppress("DEPRECATION")
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    private fun deleteComment(commentId: Long) {
        viewModel.deleteComment(commentId, parentCommentId)
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

    private fun setupUiLogic() {
        setupLoginUiLogic()
        setupCommentsUiLogic()
        setupEditingCommentUiLogic()
        setupEventUiLogic()
    }

    private fun setupLoginUiLogic() {
        viewModel.isLogin.observe(this) {
            handleNotLogin(it)
        }
    }

    private fun setupCommentsUiLogic() {
        viewModel.comments.observe(this) {
            handleErrors(it)
            handleChildComments(it)
            handleEditComment()
        }
    }

    private fun setupEditingCommentUiLogic() {
        viewModel.editingCommentContent.observe(this) {
            if (it == null) return@observe
            binding.etChildcommentsCommentUpdate.setText(it)
        }
    }

    private fun handleErrors(childComments: ChildCommentsUiState) {
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

    private fun setupEventUiLogic() {
        viewModel.event.observe(this) {
            handleEvent(it)
        }
    }

    private fun handleEvent(event: ChildCommentsEvent?) {
        if (event == null) return
        when (event) {
            ChildCommentsEvent.REPORT_ERROR -> showToast(getString(R.string.all_report_fail_message))
            ChildCommentsEvent.REPORT_COMPLETE -> InfoDialog(
                context = this,
                title = getString(R.string.all_report_complete_dialog_title),
                message = getString(R.string.all_report_complete_dialog_message),
                buttonLabel = getString(R.string.all_okay),
            ).show()

            ChildCommentsEvent.REPORT_DUPLICATION -> showToast(getString(R.string.all_report_duplicate_message))
        }
        viewModel.removeEvent()
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
            }
    }
}
