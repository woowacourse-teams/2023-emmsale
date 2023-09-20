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
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.views.InfoDialog
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.ui.childCommentList.recyclerView.ChildCommentAdapter
import com.emmsale.presentation.ui.childCommentList.recyclerView.ChildCommentRecyclerViewDivider
import com.emmsale.presentation.ui.childCommentList.uiState.ChildCommentsUiEvent
import com.emmsale.presentation.ui.childCommentList.uiState.ChildCommentsUiState
import com.emmsale.presentation.ui.eventDetail.EventDetailActivity
import com.emmsale.presentation.ui.login.LoginActivity
import com.emmsale.presentation.ui.profile.ProfileActivity

class ChildCommentActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityChildCommentsBinding.inflate(layoutInflater)
    }

    private val viewModel: ChildCommentViewModel by viewModels {
        ChildCommentViewModel.factory(parentCommentId)
    }

    private val feedId: Long by lazy { intent.getLongExtra(KEY_FEED_ID, -1) }

    private val parentCommentId: Long by lazy { intent.getLongExtra(KEY_PARENT_COMMENT_ID, -1) }

    private val inputMethodManager: InputMethodManager by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private var saveButtonCLick: Boolean = false

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

    private fun initDataBinding() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
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
        if (saveButtonCLick) scrollToLastPosition(childComments)
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

    private fun handleEvent(event: ChildCommentsUiEvent?) {
        if (event == null) return
        when (event) {
            ChildCommentsUiEvent.REPORT_ERROR -> binding.root.showSnackBar(getString(R.string.all_report_fail_message))
            ChildCommentsUiEvent.REPORT_COMPLETE -> InfoDialog(
                context = this,
                title = getString(R.string.all_report_complete_dialog_title),
                message = getString(R.string.all_report_complete_dialog_message),
                buttonLabel = getString(R.string.all_okay),
            ).show()

            ChildCommentsUiEvent.REPORT_DUPLICATE -> InfoDialog(
                context = this,
                title = getString(R.string.all_report_duplicate_dialog_title),
                message = getString(R.string.all_report_duplicate_message),
                buttonLabel = getString(R.string.all_okay),
            ).show()

            ChildCommentsUiEvent.POST_ERROR -> binding.root.showSnackBar(getString(R.string.comments_comments_posting_error_message))
            ChildCommentsUiEvent.UPDATE_ERROR -> binding.root.showSnackBar(getString(R.string.comments_comments_update_error_message))
            ChildCommentsUiEvent.DELETE_ERROR -> binding.root.showSnackBar(getString(R.string.comments_comments_delete_error_message))
        }
        viewModel.removeEvent()
    }

    private fun onChildCommentSave() {
        saveButtonCLick = true
        viewModel.saveChildComment(
            content = binding.etChildcommentsEditchildcommentcontent.text.toString(),
            parentCommentId = parentCommentId,
            feedId = feedId,
        )
        binding.etChildcommentsEditchildcommentcontent.apply {
            text.clear()
        }
        hideKeyboard()
    }

    private fun scrollToLastPosition(childComments: ChildCommentsUiState) {
        binding.rvChildcommentsChildcomments.smoothScrollToPosition(childComments.childComments.size + 1)
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
        private const val KEY_FEED_ID = "KEY_FEED_ID"
        private const val KEY_PARENT_COMMENT_ID = "KEY_PARENT_COMMENT_ID"
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
                EventDetailActivity.startActivity(this@ChildCommentActivity, feedId)
            }
            finish()
        }
    }
}
