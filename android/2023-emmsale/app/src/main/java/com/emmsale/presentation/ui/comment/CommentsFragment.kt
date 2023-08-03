package com.emmsale.presentation.ui.comment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.emmsale.R
import com.emmsale.databinding.FragmentCommentsBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.ui.childComments.ChildCommentsActivity
import com.emmsale.presentation.ui.comment.adpater.CommentsAdapter
import com.emmsale.presentation.ui.comment.uiState.CommentsScreenUiState
import com.emmsale.presentation.ui.login.LoginActivity

class CommentsFragment : BaseFragment<FragmentCommentsBinding>() {
    override val layoutResId: Int = R.layout.fragment_comments

    private val eventId: Long by lazy {
        arguments?.getLong(KEY_EVENT_ID)?.run {
            if (this < 1) throw IllegalStateException("컨퍼런스의 댓글 프래그먼트는 1 이상이어야 합니다. 로직을 다시 확인해주세요")
            this
        } ?: throw IllegalStateException("컨퍼런스의 댓글 프래그먼트는 컨퍼런스 아이디를 모르면 존재 의미가 없습니다. 로직을 다시 확인해주세요")
    }

    private val viewModel: CommentsViewModel by viewModels {
        CommentsViewModel.factory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDataBinding()
        initCommentsRecyclerView()
        setUpUiLogic()

        viewModel.fetchComments(eventId)
    }

    private fun initDataBinding() {
        binding.viewModel = viewModel
    }

    private fun initCommentsRecyclerView() {
        binding.rvCommentsComments.apply {
            adapter = CommentsAdapter(
                onChildCommentsView = ::onChildCommentsView,
                onCommentDelete = ::onCommentDelete
            )
            itemAnimator = null
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun onChildCommentsView(commentId: Long) {
        ChildCommentsActivity.startActivity(requireContext(), eventId, commentId)
    }

    private fun onCommentDelete(commentId: Long) {
        viewModel.deleteComment(commentId, eventId)
    }

    private fun setUpUiLogic() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            handleError(it)
            handleNotLogin(it)
            handleComments(it)
            handleEditComment()
        }
    }

    private fun handleError(commentsScreenUiState: CommentsScreenUiState) {
        if (commentsScreenUiState.isError) {
            context?.showToast(commentsScreenUiState.errorMessage)
        }
    }

    private fun handleNotLogin(commentsScreenUiState: CommentsScreenUiState) {
        if (commentsScreenUiState.isNotLogin) {
            LoginActivity.startActivity(requireContext())
            activity?.finish()
        }
    }

    private fun handleComments(commentsScreenUiState: CommentsScreenUiState) {
        (binding.rvCommentsComments.adapter as CommentsAdapter).submitList(commentsScreenUiState.comments)
    }

    private fun handleEditComment() {
        binding.tvCommentsPostcommentbutton.setOnClickListener { onCommentSave() }
    }

    private fun onCommentSave() {
        viewModel.saveComment(binding.etCommentsEditcommentcontent.text.toString(), eventId)
        binding.etCommentsEditcommentcontent.apply {
            text.clear()
        }
    }

    companion object {
        private const val KEY_EVENT_ID = "KEY_EVENT_ID"

        fun create(eventId: Long): CommentsFragment {
            val fragment = CommentsFragment()
            fragment.arguments = Bundle().apply {
                putLong(KEY_EVENT_ID, eventId)
            }
            return fragment
        }
    }
}