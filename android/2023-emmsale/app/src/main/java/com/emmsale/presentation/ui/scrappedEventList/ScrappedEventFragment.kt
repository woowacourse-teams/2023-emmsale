package com.emmsale.presentation.ui.scrappedEventList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.data.model.Event
import com.emmsale.databinding.FragmentScrappedEventBinding
import com.emmsale.presentation.base.NetworkFragment
import com.emmsale.presentation.common.ScrollTopListener
import com.emmsale.presentation.ui.eventDetail.EventDetailActivity
import com.emmsale.presentation.ui.scrappedEventList.recyclerView.ScrappedEventAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScrappedEventFragment : NetworkFragment<FragmentScrappedEventBinding>() {
    override val layoutResId: Int = R.layout.fragment_scrapped_event
    override val viewModel: ScrappedEventViewModel by viewModels()

    private val scrappedEventsAdapter: ScrappedEventAdapter by lazy {
        ScrappedEventAdapter(::showEventDetail)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBinding()
        observeScrappedEvents()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun setupBinding() {
        binding.vm = viewModel
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

    private fun showEventDetail(scrappedEvent: Event) {
        EventDetailActivity.startActivity(requireContext(), scrappedEvent.id)
    }
}
