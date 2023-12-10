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
import com.emmsale.presentation.ui.feedDetail.FeedDetailActivity
import com.emmsale.presentation.ui.myCommentList.recyclerView.MyCommentsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyCommentsActivity :
    NetworkActivity<ActivityMyCommentsBinding>(R.layout.activity_my_comments) {

    override val viewModel: MyCommentsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupDataBinding()
        setupToolbar()
        setupMyCommentsRecyclerView()

        observeComments()
    }

    private fun setupDataBinding() {
        binding.viewModel = viewModel
    }

    private fun setupToolbar() {
        binding.tbMycommentsToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupMyCommentsRecyclerView() {
        binding.rvMycommentsMycomments.apply {
            adapter = MyCommentsAdapter(::navigateToFeedDetail)
            itemAnimator = null
            addItemDecoration(DividerItemDecoration(this@MyCommentsActivity))
        }
    }

    private fun navigateToFeedDetail(feedId: Long, commentId: Long) {
        FeedDetailActivity.startActivity(
            context = this,
            feedId = feedId,
            highlightCommentId = commentId,
        )
    }

    private fun observeComments() {
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
