package com.emmsale.presentation.eventdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.databinding.ActivityEventDetailBinding
import com.emmsale.presentation.ui.eventdetail.EventDetailFragmentStateAdpater
import com.emmsale.presentation.ui.eventdetail.EventDetailViewModel
import com.emmsale.presentation.ui.eventdetail.EventTag
import com.emmsale.presentation.ui.eventdetail.uistate.EventDetailUiState
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
        setBackPress()
        viewModel.fetchEventDetail(1)
    }

    private fun initFragmentStateAdapter(informationUrl: String, imageUrl: String) {
        binding.vpEventdetail.adapter =
            EventDetailFragmentStateAdpater(this, eventId, informationUrl, imageUrl)
        TabLayoutMediator(binding.tablayoutEventdetail, binding.vpEventdetail) { tab, position ->
            when (position) {
                INFORMATION_TAB_POSITION -> tab.text = "상세 정보"
                COMMENT_TAB_POSITION -> tab.text = "댓글"
                PARTICIPANT_TAB_POSITION -> tab.text = "같이가요"
            }
        }.attach()
    }

    private fun setUpBinding() {
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setUpEventDetail() {
        viewModel.eventDetail.observe(this) { eventDetailUiState ->
            when (eventDetailUiState) {
                is EventDetailUiState.Success -> {
                    binding.eventDetail = eventDetailUiState
                    addTag(eventDetailUiState.tags)
                    initFragmentStateAdapter(
                        eventDetailUiState.informationUrl,
                        eventDetailUiState.imageUrl,
                    )
                }

                else -> showToastMessage("행사 받아오기 실패")
            }
        }
    }

    private fun addTag(tags: List<String>) {
        tags.forEach { binding.chipgroupEvendetailTags.addView(createTag(it)) }
    }

    private fun createTag(tag: String) = EventTag(this).apply {
        text = tag
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun setBackPress() {
        binding.ivEventdetailBackpress.setOnClickListener {
            finish()
        }
    }

    companion object {
        private const val EVENT_ID_KEY = "EVENT_ID_KEY"
        private const val DEFAULT_EVENT_ID = 1L
        private const val INFORMATION_TAB_POSITION = 0
        private const val COMMENT_TAB_POSITION = 1
        private const val PARTICIPANT_TAB_POSITION = 2

        fun startActivity(context: Context, eventId: Long) {
            val intent = Intent(context, EventDetailActivity::class.java)
            intent.putExtra(EVENT_ID_KEY, eventId)
            context.startActivity(intent)
        }
    }
}
