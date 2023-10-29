package com.emmsale.presentation.ui.myCommentList

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityMyCommentsBinding
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.recyclerView.CommonRecyclerViewDivider
import com.emmsale.presentation.ui.feedDetail.FeedDetailActivity
import com.emmsale.presentation.ui.login.LoginActivity
import com.emmsale.presentation.ui.myCommentList.recyclerView.MyCommentsAdapter
import com.emmsale.presentation.ui.myCommentList.uiState.MyCommentsUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyCommentsActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMyCommentsBinding.inflate(layoutInflater) }

    private val viewModel: MyCommentsViewModel by viewModels()

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
        binding.tvMycommentsToolbar.setNavigationOnClickListener { finish() }
    }

    private fun initMyCommentsRecyclerView() {
        binding.rvMycommentsMycomments.apply {
            adapter = MyCommentsAdapter(::navigateToFeedDetail)
            itemAnimator = null
            addItemDecoration(CommonRecyclerViewDivider(this@MyCommentsActivity))
        }
    }

    private fun navigateToFeedDetail(feedId: Long, commentId: Long) {
        FeedDetailActivity.startActivity(
            context = this,
            feedId = feedId,
            highlightCommentId = commentId,
        )
    }

    private fun setupUiLogic() {
        setupLoginUiLogic()
        setupCommentsUiLogic()
    }

    private fun setupLoginUiLogic() {
        viewModel.isLogin.observe(this) {
            handleNotLogin(it)
        }
    }

    private fun handleNotLogin(isLogin: Boolean) {
        if (!isLogin) {
            LoginActivity.startActivity(this)
            finish()
        }
    }

    private fun setupCommentsUiLogic() {
        viewModel.comments.observe(this) {
            handleErrors(it)
            handleComments(it)
        }
    }

    private fun handleErrors(comments: MyCommentsUiState) {
        handleFetchingError(comments)
    }

    private fun handleFetchingError(comments: MyCommentsUiState) {
        if (comments.isError) {
            binding.root.showSnackBar(getString(R.string.comments_comments_fetching_error_message))
        }
    }

    private fun handleComments(comments: MyCommentsUiState) {
        (binding.rvMycommentsMycomments.adapter as MyCommentsAdapter).submitList(comments.comments)
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, MyCommentsActivity::class.java))
        }
    }
}
