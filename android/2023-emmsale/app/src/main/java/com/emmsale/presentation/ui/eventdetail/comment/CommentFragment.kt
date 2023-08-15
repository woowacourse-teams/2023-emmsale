package com.emmsale.presentation.ui.eventdetail.comment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R
import com.emmsale.databinding.FragmentCommentsBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.ui.eventdetail.EventDetailActivity
import com.emmsale.presentation.ui.eventdetail.comment.childComment.ChildCommentActivity
import com.emmsale.presentation.ui.eventdetail.comment.recyclerView.CommentRecyclerViewDivider
import com.emmsale.presentation.ui.eventdetail.comment.recyclerView.CommentsAdapter
import com.emmsale.presentation.ui.eventdetail.comment.uiState.CommentsUiState
import com.emmsale.presentation.ui.login.LoginActivity

class CommentFragment : BaseFragment<FragmentCommentsBinding>() {
    override val layoutResId: Int = R.layout.fragment_comments

    private val eventId: Long by lazy {
        arguments?.getLong(KEY_EVENT_ID)?.run {
            if (this < 1) throw IllegalStateException("컨퍼런스의 댓글 프래그먼트는 1 이상이어야 합니다. 로직을 다시 확인해주세요")
            this
        } ?: throw IllegalStateException("컨퍼런스의 댓글 프래그먼트는 컨퍼런스 아이디를 모르면 존재 의미가 없습니다. 로직을 다시 확인해주세요")
    }

    private val viewModel: CommentViewModel by viewModels {
        CommentViewModel.factory
    }

    private val inputMethodManager: InputMethodManager by lazy {
        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private lateinit var eventDetailActivity: EventDetailActivity
    private var isSaveButtonClick = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventDetailActivity = context as EventDetailActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDataBinding()
        initCommentsRecyclerView()
        setupUiLogic()
        initRecyclerViewListener()
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchComments(eventId)
    }

    override fun onResume() {
        super.onResume()
        eventDetailActivity.showEventInformation()
    }

    private fun initRecyclerViewListener() {
        binding.rvCommentsComments.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                handleEventInformationVisibility(newState)
            }
        })
    }

    private fun handleEventInformationVisibility(newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE && isScrollTop()) {
            eventDetailActivity.showEventInformation()
        } else if (!isScrollTop()) {
            eventDetailActivity.hideEventInformation()
        }
    }

    private fun isScrollTop(): Boolean =
        !binding.rvCommentsComments.canScrollVertically(TOP_CONDITION)

    private fun initDataBinding() {
        binding.viewModel = viewModel
    }

    private fun initCommentsRecyclerView() {
        binding.rvCommentsComments.apply {
            adapter = CommentsAdapter(
                showChildComments = ::showChildComments,
                deleteComment = ::deleteComment,
            )
            itemAnimator = null
            addItemDecoration(CommentRecyclerViewDivider(requireContext()))
        }
    }

    private fun showChildComments(commentId: Long) {
        ChildCommentActivity.startActivity(requireContext(), eventId, commentId)
    }

    private fun deleteComment(commentId: Long) {
        viewModel.deleteComment(commentId, eventId)
    }

    private fun setupUiLogic() {
        setupLoginUiLogic()
        setupCommentsUiLogic()
    }

    private fun setupLoginUiLogic() {
        viewModel.isLogin.observe(viewLifecycleOwner) {
            handleNotLogin(it)
        }
    }

    private fun setupCommentsUiLogic() {
        viewModel.comments.observe(viewLifecycleOwner) {
            handleErrors(it)
            handleComments(it)
            handleCommentEditing()
        }
    }

    private fun handleErrors(comments: CommentsUiState) {
        fun handleCommentsFetchingError(comments: CommentsUiState) {
            if (comments.isFetchingError) {
                context?.showToast(getString(R.string.comments_comments_fetching_error_message))
            }
        }

        fun handleCommentPostingError(comments: CommentsUiState) {
            if (comments.isPostingError) {
                context?.showToast(getString(R.string.comments_comments_posting_error_message))
            }
        }

        fun handleCommentDeletionError(comments: CommentsUiState) {
            if (comments.isDeletionError) {
                context?.showToast(getString(R.string.comments_comments_deletion_error_message))
            }
        }
        handleCommentsFetchingError(comments)
        handleCommentPostingError(comments)
        handleCommentDeletionError(comments)
    }

    private fun handleNotLogin(isLogin: Boolean) {
        if (!isLogin) {
            LoginActivity.startActivity(requireContext())
            activity?.finish()
        }
    }

    private fun handleComments(comments: CommentsUiState) {
        (binding.rvCommentsComments.adapter as CommentsAdapter).submitList(comments.comments)
        if (isSaveButtonClick) scrollToLastPosition(comments)
        isSaveButtonClick = false
    }

    private fun handleCommentEditing() {
        binding.tvCommentsPostcommentbutton.setOnClickListener { onCommentSave() }
    }

    private fun onCommentSave() {
        isSaveButtonClick = true
        viewModel.saveComment(binding.etCommentsEditcommentcontent.text.toString(), eventId)
        binding.etCommentsEditcommentcontent.apply {
            text.clear()
        }
        hideKeyboard()
    }

    private fun scrollToLastPosition(comments: CommentsUiState) {
        binding.rvCommentsComments.smoothScrollToPosition(comments.comments.size + 1)
    }

    private fun hideKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    companion object {
        private const val KEY_EVENT_ID = "KEY_EVENT_ID"
        private const val TOP_CONDITION = -1
        fun create(eventId: Long): CommentFragment {
            val fragment = CommentFragment()
            fragment.arguments = Bundle().apply {
                putLong(KEY_EVENT_ID, eventId)
            }
            return fragment
        }
    }
}
