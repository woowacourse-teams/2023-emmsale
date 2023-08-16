package com.emmsale.presentation.ui.setting.myPost

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.emmsale.R
import com.emmsale.databinding.ActivityMyPostBinding
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.ui.eventdetail.recruitment.detail.RecruitmentPostDetailActivity
import com.emmsale.presentation.ui.setting.myPost.recyclerView.MyPostAdapter

class MyPostActivity : AppCompatActivity() {
    private val binding: ActivityMyPostBinding by lazy {
        ActivityMyPostBinding.inflate(layoutInflater)
    }
    private val viewModel: MyPostViewModel by viewModels { MyPostViewModel.factory }

    private val myPostAdapter: MyPostAdapter by lazy {
        MyPostAdapter(navigateToDetail = ::navigateToDetail)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initRecyclerView()
        setUpMyPosts()
    }

    private fun setUpMyPosts() {
        viewModel.myPosts.observe(this) { myPosts ->
            if (myPosts.isError) {
                showToast(R.string.all_data_loading_failed_message)
            } else {
                myPostAdapter.submitList(myPosts.list)
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvMyPost.layoutManager = LinearLayoutManager(this)
        binding.rvMyPost.adapter = myPostAdapter
    }

    private fun navigateToDetail(eventId: Long, recruitmentId: Long) {
        startActivity(
            RecruitmentPostDetailActivity.getIntent(
                this,
                eventId = eventId,
                recruitmentId = recruitmentId,
                isNavigatedFromMyPost = true,
            ),
        )
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, MyPostActivity::class.java)
            context.startActivity(intent)
        }
    }
}
