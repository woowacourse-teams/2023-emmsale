package com.emmsale.presentation.ui.eventdetail

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityEventDetailBinding
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.ui.main.MainActivity
import com.google.android.material.tabs.TabLayoutMediator

class EventDetailActivity : AppCompatActivity() {
    private val binding: ActivityEventDetailBinding by lazy {
        ActivityEventDetailBinding.inflate(layoutInflater)
    }
    private val eventId: Long by lazy {
        intent.getLongExtra(EVENT_ID_KEY, DEFAULT_EVENT_ID)
    }
    private val viewModel: EventDetailViewModel by viewModels { EventDetailViewModel.factory(eventId) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBackPressedDispatcher()
        setUpBinding()
        setUpEventDetail()
        initBackPressButtonClickListener()
    }

    fun hideEventInformation() {
        binding.clEventDetailEventContainer.visibility = View.GONE
    }

    fun showEventInformation() {
        binding.clEventDetailEventContainer.visibility = View.VISIBLE
    }

    private fun initBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(this, EventDetailOnBackPressedCallback())
    }

    private fun setUpBinding() {
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.vm = viewModel
    }

    private fun setUpEventDetail() {
        viewModel.eventDetail.observe(this) { eventDetailUiState ->
            if (eventDetailUiState.isError) {
                showToast(getString(R.string.eventdetail_fetch_eventdetail_error_message))
            } else {
                addEventTag(eventDetailUiState.tags)
                initFragmentStateAdapter(
                    eventDetailUiState.informationUrl,
                    eventDetailUiState.imageUrl,
                )
            }
        }
    }

    private fun initFragmentStateAdapter(informationUrl: String, imageUrl: String?) {
        binding.vpEventdetail.adapter =
            EventDetailFragmentStateAdapter(this, eventId, informationUrl, imageUrl)
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

    private fun addEventTag(tags: List<String>) {
        tags.forEach { binding.chipgroupEvendetailTags.addView(createEventTag(it)) }
    }

    private fun createEventTag(tag: String) = EventTag(this).apply {
        text = tag
    }

    private fun initBackPressButtonClickListener() {
        binding.ivEventdetailBackpress.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
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
            val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            if (activityManager.appTasks.size == 1) MainActivity.startActivity(this@EventDetailActivity)
        }
    }
}
