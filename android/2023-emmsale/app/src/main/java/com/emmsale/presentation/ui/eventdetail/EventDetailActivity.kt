package com.emmsale.presentation.ui.eventdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityEventDetailBinding
import com.emmsale.presentation.common.extension.showToast
import com.google.android.material.tabs.TabLayoutMediator

class EventDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventDetailBinding
    private val viewModel: EventDetailViewModel by viewModels { EventDetailViewModel.factory }
    private val eventId: Long by lazy {
        intent.getLongExtra(EVENT_ID_KEY, DEFAULT_EVENT_ID)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpBinding()
        setUpEventDetail()
        initBackPressButtonClickListener()
        viewModel.fetchEventDetail(eventId)
    }

    private fun setUpBinding() {
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        setContentView(binding.root)
    }

    private fun setUpEventDetail() {
        viewModel.eventDetail.observe(this) { eventDetailUiState ->
            if (eventDetailUiState.isError) {
                showToast(getString(R.string.eventdetail_fetch_eventdetail_error_message))
            } else {
                addTag(eventDetailUiState.tags)
                initFragmentStateAdapter(
                    eventDetailUiState.informationUrl,
                    eventDetailUiState.imageUrl,
                )
            }
        }
    }

    private fun initFragmentStateAdapter(informationUrl: String, imageUrl: String?) {
        binding.vpEventdetail.adapter =
            EventDetailFragmentStateAdpater(this, eventId, informationUrl, imageUrl)
        TabLayoutMediator(binding.tablayoutEventdetail, binding.vpEventdetail) { tab, position ->
            when (position) {
                INFORMATION_TAB_POSITION ->
                    tab.text = getString(R.string.eventdetail_tab_infromation)

                COMMENT_TAB_POSITION -> tab.text = getString(R.string.eventdetail_tab_comment)
                RECRUITMENT_TAB_POSITION ->
                    tab.text = getString(R.string.eventdetail_tab_recruitment)
            }
        }.attach()
        binding.vpEventdetail.isUserInputEnabled = false
    }

    private fun addTag(tags: List<String>) {
        tags.forEach { binding.chipgroupEvendetailTags.addView(createTag(it)) }
    }

    private fun createTag(tag: String) = EventTag(this).apply {
        text = tag
    }

    private fun initBackPressButtonClickListener() {
        binding.ivEventdetailBackpress.setOnClickListener {
            finish()
        }
    }

    companion object {
        private const val EVENT_ID_KEY = "EVENT_ID_KEY"
        private const val DEFAULT_EVENT_ID = 1L
        private const val INFORMATION_TAB_POSITION = 0
        private const val COMMENT_TAB_POSITION = 1
        private const val RECRUITMENT_TAB_POSITION = 2

        fun startActivity(context: Context, eventId: Long) {
            val intent = Intent(context, EventDetailActivity::class.java)
            intent.putExtra(EVENT_ID_KEY, eventId)
            context.startActivity(intent)
        }
    }
}
