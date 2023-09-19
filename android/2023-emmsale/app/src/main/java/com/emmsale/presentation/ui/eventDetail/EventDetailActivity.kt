package com.emmsale.presentation.ui.eventdetail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityEventDetailBinding
import com.emmsale.presentation.common.Event
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegate
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegateImpl
import com.emmsale.presentation.ui.eventDetail.EventDetailFragmentStateAdapter
import com.emmsale.presentation.ui.eventDetail.EventDetailViewModel
import com.emmsale.presentation.ui.eventDetailInfo.uiState.EventInfoUiEvent
import com.emmsale.presentation.ui.main.MainActivity
import com.google.android.material.tabs.TabLayoutMediator

class EventDetailActivity :
    AppCompatActivity(),
    FirebaseAnalyticsDelegate by FirebaseAnalyticsDelegateImpl("event_detail") {
    private val binding: ActivityEventDetailBinding by lazy {
        ActivityEventDetailBinding.inflate(layoutInflater)
    }
    private val eventId: Long by lazy {
        intent.getLongExtra(EVENT_ID_KEY, DEFAULT_EVENT_ID)
    }
    private val viewModel: EventDetailViewModel by viewModels { EventDetailViewModel.factory(eventId) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerScreen(this)
        initFragmentStateAdapter()
        initBackPressedDispatcher()
        setUpBinding()
        setUpEventUiEvent()
        initBackPressButtonClickListener()
    }

    private fun initBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(this, EventDetailOnBackPressedCallback())
    }

    private fun setUpBinding() {
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        binding.navigateToUrl = ::navigateToUrl
    }

    private fun setUpEventUiEvent() {
        viewModel.scrapUiEvent.observe(this, ::handleEvent)
    }

    private fun handleEvent(event: Event<EventInfoUiEvent>) {
        val content = event.getContentIfNotHandled() ?: return
        when (content) {
            EventInfoUiEvent.SCRAP_ERROR -> binding.root.showSnackBar(getString(R.string.eventdetail_scrap_error))
            EventInfoUiEvent.SCRAP_DELETE_ERROR -> binding.root.showSnackBar(getString(R.string.eventdetail_scrap_delete_error))
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
            EventDetailFragmentStateAdapter(this, eventId)
        val tabNames = listOf(
            getString(R.string.eventdetail_tab_infromation),
            getString(R.string.eventdetail_tab_comment),
            getString(R.string.eventdetail_tab_recruitment),
        )
        TabLayoutMediator(binding.tablayoutEventdetail, binding.vpEventdetail) { tab, position ->
            tab.text = tabNames[position]
        }.attach()
        binding.vpEventdetail.isUserInputEnabled = false
    }

    private fun initBackPressButtonClickListener() {
        binding.tbEventdetail.setNavigationOnClickListener { finish() }
    }

    companion object {
        private const val EVENT_ID_KEY = "EVENT_ID_KEY"
        private const val DEFAULT_EVENT_ID = 1L

        fun startActivity(context: Context, eventId: Long) {
            val intent = Intent(context, EventDetailActivity::class.java)
            intent.putExtra(EVENT_ID_KEY, eventId)
            context.startActivity(intent)
        }

        fun getIntent(context: Context, eventId: Long): Intent =
            Intent(context, EventDetailActivity::class.java).apply {
                putExtra(EVENT_ID_KEY, eventId)
            }
    }

    inner class EventDetailOnBackPressedCallback : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
            if (isTaskRoot) MainActivity.startActivity(this@EventDetailActivity)
        }
    }
}
