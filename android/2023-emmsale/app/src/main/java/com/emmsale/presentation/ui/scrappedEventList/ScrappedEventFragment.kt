package com.emmsale.presentation.ui.scrappedEventList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentScrappedEventBinding
import com.emmsale.model.Event
import com.emmsale.presentation.base.NetworkFragment
import com.emmsale.presentation.common.ScrollTopListener
import com.emmsale.presentation.ui.eventDetail.EventDetailActivity
import com.emmsale.presentation.ui.scrappedEventList.recyclerView.ScrappedEventAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScrappedEventFragment :
    NetworkFragment<FragmentScrappedEventBinding>(R.layout.fragment_scrapped_event) {

    override val viewModel: ScrappedEventViewModel by viewModels()

    private val scrappedEventsAdapter: ScrappedEventAdapter by lazy {
        ScrappedEventAdapter(::navigateToEventDetail)
    }

    private fun navigateToEventDetail(scrappedEvent: Event) {
        EventDetailActivity.startActivity(requireContext(), scrappedEvent.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDataBinding()
        setupScrappedEventsRecyclerView()

        observeScrappedEvents()
    }

    private fun setupDataBinding() {
        binding.vm = viewModel
    }

    private fun setupScrappedEventsRecyclerView() {
        binding.rvScrappedEvents.adapter = scrappedEventsAdapter
        binding.rvScrappedEvents.addOnScrollListener(
            ScrollTopListener(targetView = binding.fabScrollTop),
        )
    }

    private fun observeScrappedEvents() {
        viewModel.scrappedEvents.observe(viewLifecycleOwner) { scrappedEvents ->
            scrappedEventsAdapter.submitList(scrappedEvents)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }
}
