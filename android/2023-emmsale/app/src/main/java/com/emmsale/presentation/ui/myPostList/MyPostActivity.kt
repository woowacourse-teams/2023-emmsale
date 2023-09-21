package com.emmsale.presentation.ui.myPostList

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.emmsale.R
import com.emmsale.databinding.ActivityMyPostBinding
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.ui.myPostList.recyclerView.MyPostAdapter
import com.emmsale.presentation.ui.recruitmentDetail.RecruitmentPostDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPostActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMyPostBinding.inflate(layoutInflater) }
    private val viewModel: MyPostViewModel by viewModels()

    private val myPostAdapter: MyPostAdapter by lazy {
        MyPostAdapter(navigateToDetail = ::navigateToDetail)
    }

    private val fetchByResultActivityLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result == null || result.resultCode != RESULT_OK) return@registerForActivityResult
            viewModel.refresh()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initRecyclerView()
        setUpMyPosts()
        initBackPressButton()
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
        val intent = RecruitmentPostDetailActivity.getIntent(
            this,
            eventId = eventId,
            recruitmentId = recruitmentId,
            isNavigatedFromMyPost = true,
        )
        fetchByResultActivityLauncher.launch(intent)
    }

    private fun initBackPressButton() {
        binding.tbMyPost.setNavigationOnClickListener {
            finish()
        }
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, MyPostActivity::class.java)
            context.startActivity(intent)
        }
    }
}
