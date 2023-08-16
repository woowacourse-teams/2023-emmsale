package com.emmsale.presentation.ui.setting.myComments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityMyCommentsBinding
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.ui.eventdetail.comment.childComment.ChildCommentActivity
import com.emmsale.presentation.ui.eventdetail.comment.recyclerView.CommentRecyclerViewDivider
import com.emmsale.presentation.ui.login.LoginActivity
import com.emmsale.presentation.ui.setting.myComments.recyclerView.MyCommentsAdapter
import com.emmsale.presentation.ui.setting.myComments.uiState.MyCommentsUiState

class MyCommentsActivity : AppCompatActivity() {

    private val binding: ActivityMyCommentsBinding by lazy {
        ActivityMyCommentsBinding.inflate(layoutInflater)
    }

    private val viewModel: MyCommentsViewModel by viewModels {
        MyCommentsViewModel.factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initDataBinding()
        initToolbar()
        initMyCommentsRecyclerView()
        setupUiLogic()

        viewModel.fetchMyComments()
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
            adapter = MyCommentsAdapter(::showChildComments)
            itemAnimator = null
            addItemDecoration(CommentRecyclerViewDivider(this@MyCommentsActivity))
        }
    }

    private fun showChildComments(eventId: Long, commentId: Long) {
        ChildCommentActivity.startActivity(this, eventId, commentId)
    }

    fun setupUiLogic() {
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
        if (comments.isFetchingError) {
            showToast(getString(R.string.comments_comments_fetching_error_message))
        }
    }

    private fun handleComments(comments: MyCommentsUiState) {
        Log.d("THOMAS", "댓글 : " + comments.comments.toString())
        (binding.rvMycommentsMycomments.adapter as MyCommentsAdapter).submitList(comments.comments)
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, MyCommentsActivity::class.java))
        }
    }
}
