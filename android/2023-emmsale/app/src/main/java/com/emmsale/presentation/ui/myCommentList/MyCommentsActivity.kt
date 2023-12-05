package com.emmsale.presentation.ui.myCommentList

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.emmsale.R
import com.emmsale.data.model.Comment
import com.emmsale.databinding.ActivityMyCommentsBinding
import com.emmsale.presentation.base.NetworkActivity
import com.emmsale.presentation.common.recyclerView.DividerItemDecoration
import com.emmsale.presentation.ui.childCommentList.ChildCommentActivity
import com.emmsale.presentation.ui.myCommentList.recyclerView.MyCommentsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyCommentsActivity :
    NetworkActivity<ActivityMyCommentsBinding>(R.layout.activity_my_comments) {

    override val viewModel: MyCommentsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initDataBinding()
        initToolbar()
        initMyCommentsRecyclerView()
        setupUiLogic()
    }

    fun initDataBinding() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun initToolbar() {
        binding.tbMycommentsToolbar.setNavigationOnClickListener { finish() }
    }

    private fun initMyCommentsRecyclerView() {
        binding.rvMycommentsMycomments.apply {
            adapter = MyCommentsAdapter(::navigateToChildComments)
            itemAnimator = null
            addItemDecoration(DividerItemDecoration(this@MyCommentsActivity))
        }
    }

    private fun navigateToChildComments(eventId: Long, parentCommentId: Long, commentId: Long) {
        ChildCommentActivity.startActivity(
            context = this,
            feedId = eventId,
            parentCommentId = parentCommentId,
            highlightCommentId = commentId,
            fromPostDetail = false,
        )
    }

    private fun setupUiLogic() {
        setupCommentsUiLogic()
    }

    private fun setupCommentsUiLogic() {
        viewModel.comments.observe(this) {
            handleComments(it)
        }
    }

    private fun handleComments(comments: List<Comment>) {
        (binding.rvMycommentsMycomments.adapter as MyCommentsAdapter).submitList(comments)
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, MyCommentsActivity::class.java))
        }
    }
}
