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
    private val eventId: Long by lazy {
        intent.getLongExtra(EVENT_ID_KEY, DEFAULT_EVENT_ID)
    }
    private val viewModel: EventDetailViewModel by viewModels { EventDetailViewModel.factory(eventId) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpBinding()
        setUpEventDetail()
        initBackPressButtonClickListener()
    }

    private fun setUpBinding() {
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
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
            EventDetailFragmentStateAdpater(this, eventId, informationUrl, imageUrl)
        val tabNames = listOf(
            getString(R.string.eventdetail_tab_infromation),
            getString(R.string.eventdetail_tab_comment),
            getString(R.string.eventdetail_tab_recruitment),
        )
        TabLayoutMediator(binding.tablayoutEventdetail, binding.vpEventdetail) { _, _ ->
            tabNames
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
            finish()
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
}
