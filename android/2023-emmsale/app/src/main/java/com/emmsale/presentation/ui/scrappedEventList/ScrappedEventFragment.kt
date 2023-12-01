package com.emmsale.presentation.ui.scrappedEventList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentScrappedEventBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.CommonUiEvent
import com.emmsale.presentation.common.ScrollTopListener
import com.emmsale.presentation.common.extension.showToast
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
        setupBinding()
        observeScrappedEvents()
        observeCommonUiEvent()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isFirstFetch) {
            viewModel.fetchScrappedEvents()
            viewModel.isFirstFetch = false
        } else {
            viewModel.refresh()
        }
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
            scrappedEventsAdapter.submitList(scrappedEvents.list)
        }
    }

    private fun observeCommonUiEvent() {
        viewModel.commonUiEvent.observe(viewLifecycleOwner) { commonUiEvent ->
            when (commonUiEvent) {
                CommonUiEvent.RequestFailByNetworkError -> showToast(getString(R.string.all_network_error_message))
                is CommonUiEvent.Unexpected -> showToast(getString(R.string.all_network_error_message))
            }
        }
    }

    private fun showEventDetail(scrappedEventUiState: ScrappedEventUiState) {
        EventDetailActivity.startActivity(requireContext(), scrappedEventUiState.event.id)
    }
}
