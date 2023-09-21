package com.emmsale.presentation.ui.commentList

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentCommentsBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegate
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegateImpl
import com.emmsale.presentation.common.views.InfoDialog
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.ui.childCommentList.ChildCommentActivity
import com.emmsale.presentation.ui.commentList.CommentViewModel.Companion.KEY_EVENT_ID
import com.emmsale.presentation.ui.commentList.recyclerView.CommentRecyclerViewDivider
import com.emmsale.presentation.ui.commentList.recyclerView.CommentsAdapter
import com.emmsale.presentation.ui.commentList.uiState.CommentsUiEvent
import com.emmsale.presentation.ui.commentList.uiState.CommentsUiState
import com.emmsale.presentation.ui.eventDetail.EventDetailActivity
import com.emmsale.presentation.ui.login.LoginActivity
import com.emmsale.presentation.ui.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentFragment :
    BaseFragment<FragmentCommentsBinding>(),
    FirebaseAnalyticsDelegate by FirebaseAnalyticsDelegateImpl("comment") {
    override val layoutResId: Int = R.layout.fragment_comments
    private val viewModel: CommentViewModel by viewModels()

    private val inputMethodManager: InputMethodManager by lazy {
        requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private lateinit var eventDetailActivity: EventDetailActivity
    private var isSaveButtonClick = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventDetailActivity = context as EventDetailActivity
        registerScreen(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDataBinding()
        initCommentsRecyclerView()
        setupUiLogic()
    }

    override fun onStart() {
        super.onStart()
        viewModel.refresh()
    }

    private fun initDataBinding() {
        binding.viewModel = viewModel
        binding.cancelUpdateComment = ::cancelUpdateComment
        binding.updateComment = ::updateComment
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

    private fun initCommentsRecyclerView() {
        binding.rvCommentsComments.apply {
            adapter = CommentsAdapter(
                showProfile = ::showProfile,
                showChildComments = ::showChildComments,
                editComment = ::editComment,
                deleteComment = ::deleteComment,
                reportComment = ::reportComment,
            )
            itemAnimator = null
            addItemDecoration(CommentRecyclerViewDivider(requireContext()))
        }
    }

    private fun showProfile(authorId: Long) {
        ProfileActivity.startActivity(context ?: return, authorId)
    }

    private fun showChildComments(commentId: Long) {
        ChildCommentActivity.startActivity(requireContext(), viewModel.eventId, commentId)
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
        val context = context ?: return
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

    private fun setupUiLogic() {
        setupLoginUiLogic()
        setupCommentsUiLogic()
        setupEditingCommentUiLogic()
        setupEventUiLogic()
    }

    private fun setupLoginUiLogic() {
        viewModel.isLogin.observe(viewLifecycleOwner) {
            handleNotLogin(it)
        }
    }

    private fun setupCommentsUiLogic() {
        viewModel.comments.observe(viewLifecycleOwner) {
            handleComments(it)
            handleCommentEditing()
        }
    }

    private fun handleNotLogin(isLogin: Boolean) {
        if (!isLogin) {
            LoginActivity.startActivity(requireContext())
            activity?.finish()
        }
    }

    private fun handleComments(comments: CommentsUiState) {
        (binding.rvCommentsComments.adapter as CommentsAdapter).submitList(comments.comments)
    }

    private fun handleCommentEditing() {
        binding.tvCommentsPostcommentbutton.setOnClickListener { onCommentSave() }
    }

    private fun onCommentSave() {
        isSaveButtonClick = true
        viewModel.saveComment(binding.etCommentsPostComment.text.toString())
        binding.etCommentsPostComment.apply {
            text.clear()
        }
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

    private fun setupEditingCommentUiLogic() {
        viewModel.editingCommentContent.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            binding.etCommentsCommentUpdate.setText(it)
        }
    }

    private fun setupEventUiLogic() {
        viewModel.event.observe(viewLifecycleOwner) {
            handleEvents(it)
        }
    }

    private fun handleEvents(event: CommentsUiEvent?) {
        if (event == null) return
        when (event) {
            CommentsUiEvent.REPORT_ERROR -> binding.root.showSnackBar(R.string.all_report_fail_message)
            CommentsUiEvent.REPORT_COMPLETE -> InfoDialog(
                context = context ?: return,
                title = getString(R.string.all_report_complete_dialog_title),
                message = getString(R.string.all_report_complete_dialog_message),
                buttonLabel = getString(R.string.all_okay),
            ).show()

            CommentsUiEvent.POST_ERROR -> binding.root.showSnackBar(getString(R.string.comments_comments_posting_error_message))
            CommentsUiEvent.UPDATE_ERROR -> binding.root.showSnackBar(getString(R.string.comments_comments_update_error_message))
            CommentsUiEvent.DELETE_ERROR -> binding.root.showSnackBar(getString(R.string.comments_comments_delete_error_message))
            CommentsUiEvent.REPORT_DUPLICATE -> InfoDialog(
                context = context ?: return,
                title = getString(R.string.all_report_duplicate_dialog_title),
                message = getString(R.string.all_report_duplicate_message),
                buttonLabel = getString(R.string.all_okay),
            ).show()
        }
        viewModel.removeEvent()
    }

    companion object {
        fun create(eventId: Long): CommentFragment = CommentFragment().apply {
            arguments = Bundle().apply {
                putLong(KEY_EVENT_ID, eventId)
            }
        }
    }
}
