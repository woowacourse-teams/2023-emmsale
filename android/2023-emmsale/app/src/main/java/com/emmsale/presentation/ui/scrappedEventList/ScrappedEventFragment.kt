package com.emmsale.presentation.ui.scrappedEventList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentScrappedEventBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.ScrollUpVisibilityListener
import com.emmsale.presentation.ui.eventDetail.EventDetailActivity
import com.emmsale.presentation.ui.scrappedEventList.recyclerView.ScrappedEventAdapter
import com.emmsale.presentation.ui.scrappedEventList.uiState.ScrappedEventUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScrappedEventFragment : BaseFragment<FragmentScrappedEventBinding>() {
    override val layoutResId: Int = R.layout.fragment_scrapped_event
    private val viewModel: ScrappedEventViewModel by viewModels()

    private val scrappedEventsAdapter: ScrappedEventAdapter by lazy {
        ScrappedEventAdapter(::showEventDetail)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        setUpScrappedEvents()
        initScrollTopFabClickListener()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun initBinding() {
        binding.vm = viewModel
        binding.rvScrappedEvents.adapter = scrappedEventsAdapter
        binding.rvScrappedEvents.addOnScrollListener(
            ScrollUpVisibilityListener(requireContext(), binding.fabScrollTop),
        )
    }

    private fun setUpScrappedEvents() {
        viewModel.scrappedEvents.observe(viewLifecycleOwner) { scrappedEvents ->
            scrappedEventsAdapter.submitList(scrappedEvents.list)
        }
    }

    private fun initScrollTopFabClickListener() {
        binding.fabScrollTop.setOnClickListener { binding.rvScrappedEvents.smoothScrollToPosition(0) }
    }

    private fun showEventDetail(scrappedEventUiState: ScrappedEventUiState) {
        EventDetailActivity.startActivity(requireContext(), scrappedEventUiState.scrappedEvent.id)
    }
}
