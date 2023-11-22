package com.emmsale.presentation.ui.eventDetail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityEventDetailBinding
import com.emmsale.presentation.common.UiEvent
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegate
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegateImpl
import com.emmsale.presentation.ui.eventDetail.EventDetailViewModel.Companion.EVENT_ID_KEY
import com.emmsale.presentation.ui.eventDetail.uiState.EventDetailScreenUiState
import com.emmsale.presentation.ui.eventDetailInfo.uiState.EventInfoUiEvent
import com.emmsale.presentation.ui.feedWriting.FeedWritingActivity
import com.emmsale.presentation.ui.main.MainActivity
import com.emmsale.presentation.ui.recruitmentWriting.RecruitmentPostWritingActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventDetailActivity :
    AppCompatActivity(),
    FirebaseAnalyticsDelegate by FirebaseAnalyticsDelegateImpl("event_detail") {
    private val binding by lazy { ActivityEventDetailBinding.inflate(layoutInflater) }
    private val viewModel: EventDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerScreen(this)
        initFragmentStateAdapter()
        initBackPressedDispatcher()
        setUpBinding()
        setUpScrapUiEvent()
        setUpRecruitmentWritingPermission()
        initBackPressButtonClickListener()
        onTabSelectedListener()
    }

    private fun initBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(this, EventDetailOnBackPressedCallback())
    }

    private fun setUpBinding() {
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        binding.navigateToUrl = ::navigateToUrl
        binding.navigateToWritingPost = ::navigateToWriting
    }

    private fun setUpScrapUiEvent() {
        viewModel.scrapUiEvent.observe(this) { event ->
            handleEvent(event)
        }
    }

    private fun handleEvent(event: UiEvent<EventInfoUiEvent>) {
        val content = event.getContentIfNotHandled() ?: return
        when (content) {
            EventInfoUiEvent.SCRAP_ERROR -> binding.root.showSnackBar("스크랩 불가")
            EventInfoUiEvent.SCRAP_DELETE_ERROR -> binding.root.showSnackBar("스크랩 삭제 불가")
        }
    }

    private fun setUpRecruitmentWritingPermission() {
        viewModel.hasWritingPermission.observe(this) {
            val hasPermission = it.getContentIfNotHandled() ?: return@observe
            if (hasPermission) {
                navigateToRecruitmentWriting()
            } else {
                binding.root.showSnackBar(getString(R.string.eventrecruitment_has_not_permission_writing))
            }
        }
    }

    private fun navigateToRecruitmentWriting() {
        startActivity(RecruitmentPostWritingActivity.getPostModeIntent(this, viewModel.eventId))
    }

    private fun navigateToWriting() {
        when (viewModel.currentScreen.value) {
            EventDetailScreenUiState.INFORMATION -> Unit
            EventDetailScreenUiState.RECRUITMENT -> viewModel.fetchHasWritingPermission()
            EventDetailScreenUiState.POST -> {
                FeedWritingActivity.startActivity(this, viewModel.eventId)
            }
        }
    }

    private fun navigateToUrl(url: String) {
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url),
        )
        startActivity(browserIntent)
    }

    private fun initFragmentStateAdapter() {
        binding.vpEventdetail.adapter =
            EventDetailFragmentStateAdapter(this, viewModel.eventId)
        val tabNames = listOf(
            getString(R.string.eventdetail_tab_infromation),
            getString(R.string.eventdetail_tab_post),
            getString(R.string.eventdetail_tab_recruitment),
        )
        TabLayoutMediator(binding.tablayoutEventdetail, binding.vpEventdetail) { tab, position ->
            tab.text = tabNames[position]
        }.attach()
        binding.vpEventdetail.isUserInputEnabled = false
    }

    private fun onTabSelectedListener() {
        binding.tablayoutEventdetail.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    viewModel.fetchCurrentScreen(tab?.position ?: 0)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) = Unit
                override fun onTabReselected(tab: TabLayout.Tab?) = Unit
            },
        )
    }

    private fun initBackPressButtonClickListener() {
        binding.tbEventdetail.setNavigationOnClickListener { finish() }
    }

    companion object {
        fun startActivity(context: Context, eventId: Long) {
            val intent = Intent(context, EventDetailActivity::class.java)
                .putExtra(EVENT_ID_KEY, eventId)
            context.startActivity(intent)
        }

        fun getIntent(context: Context, eventId: Long): Intent =
            Intent(context, EventDetailActivity::class.java).putExtra(EVENT_ID_KEY, eventId)
    }

    inner class EventDetailOnBackPressedCallback : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
            if (isTaskRoot) MainActivity.startActivity(this@EventDetailActivity)
        }
    }
}
