package com.emmsale.presentation.ui.myRecruitmentList

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
import com.emmsale.presentation.ui.myRecruitmentList.recyclerView.MyRecruitmentAdapter
import com.emmsale.presentation.ui.recruitmentDetail.RecruitmentPostDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyRecruitmentActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMyPostBinding.inflate(layoutInflater) }
    private val viewModel: MyRecruitmentViewModel by viewModels()

    private val myRecruitmentAdapter: MyRecruitmentAdapter by lazy {
        MyRecruitmentAdapter(navigateToDetail = ::navigateToDetail)
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
        viewModel.myRecruitments.observe(this) { myRecruitments ->
            if (myRecruitments.isError) {
                showToast(R.string.all_data_loading_failed_message)
            } else {
                myRecruitmentAdapter.submitList(myRecruitments.list)
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvMyPost.layoutManager = LinearLayoutManager(this)
        binding.rvMyPost.adapter = myRecruitmentAdapter
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
            val intent = Intent(context, MyRecruitmentActivity::class.java)
            context.startActivity(intent)
        }
    }
}
