package com.emmsale.presentation.ui.eventDetail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.emmsale.R
import com.emmsale.databinding.ActivityEventDetailBinding
import com.emmsale.presentation.base.NetworkActivity
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegate
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegateImpl
import com.emmsale.presentation.ui.eventDetail.EventDetailViewModel.Companion.EVENT_ID_KEY
import com.emmsale.presentation.ui.eventDetail.uiState.EventDetailScreenUiState
import com.emmsale.presentation.ui.eventDetail.uiState.EventDetailUiEvent
import com.emmsale.presentation.ui.feedWriting.FeedWritingActivity
import com.emmsale.presentation.ui.main.MainActivity
import com.emmsale.presentation.ui.recruitmentWriting.RecruitmentWritingActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventDetailActivity :
    NetworkActivity<ActivityEventDetailBinding>(R.layout.activity_event_detail),
    FirebaseAnalyticsDelegate by FirebaseAnalyticsDelegateImpl("event_detail") {

    override val viewModel: EventDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        registerScreen(this)

        fetchEvent()
        setupDataBinding()
        setupFragmentStateAdapter()
        setupBackPressedDispatcher()
        setupToolbar()
        setupOnTabSelectedListener()

        observeUiEvent()
    }

    private fun fetchEvent() {
        val eventId = intent.data?.getQueryParameter(EVENT_ID_KEY)?.toLong()
        if (eventId != null) viewModel.eventId = eventId

        viewModel.fetchEvent()
        viewModel.fetchIsScrapped()
    }

    private fun setupDataBinding() {
        binding.vm = viewModel
        binding.navigateToUrl = ::navigateToUrl
        binding.navigateToWritingPost = ::navigateToWriting
    }

    private fun navigateToUrl(url: String) {
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url),
        )
        startActivity(browserIntent)
    }

    private fun navigateToWriting() {
        when (viewModel.currentScreen.value) {
            EventDetailScreenUiState.INFORMATION -> Unit
            EventDetailScreenUiState.RECRUITMENT -> viewModel.checkIsAlreadyPostRecruitment()
            EventDetailScreenUiState.POST -> {
                FeedWritingActivity.startActivity(this, viewModel.eventId)
            }
        }
    }

    private fun setupFragmentStateAdapter() {
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
    }

    private fun setupBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                    if (isTaskRoot) MainActivity.startActivity(this@EventDetailActivity)
                }
            },
        )
    }

    private fun setupToolbar() {
        binding.tbEventdetail.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.tbEventdetail.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.share_event) viewModel.shareEvent()
            true
        }
    }

    private fun setupOnTabSelectedListener() {
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

    private fun observeUiEvent() {
        viewModel.uiEvent.observe(this, ::handleUiEvent)
    }

    private fun handleUiEvent(uiEvent: EventDetailUiEvent) {
        when (uiEvent) {
            EventDetailUiEvent.ScrapFail -> binding.root.showSnackBar(R.string.eventdetail_scrap_fail)
            EventDetailUiEvent.ScrapOffFail -> binding.root.showSnackBar(R.string.eventdetail_scrap_off_fail)
            EventDetailUiEvent.RecruitmentIsAlreadyPosted -> binding.root.showSnackBar(R.string.eventrecruitment_has_not_permission_writing)
            EventDetailUiEvent.RecruitmentPostApproval -> navigateToRecruitmentWriting()
            EventDetailUiEvent.RecruitmentPostedCheckFail -> binding.root.showSnackBar(R.string.eventrecruitment_has_not_permission_writing_check_fail_message)
        }
    }

    private fun navigateToRecruitmentWriting() {
        startActivity(RecruitmentWritingActivity.getPostModeIntent(this, viewModel.eventId))
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
}
