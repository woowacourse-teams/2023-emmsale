package com.emmsale.presentation.ui.main.event.scrap

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentScrappedEventBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.ui.eventdetail.EventDetailActivity
import com.emmsale.presentation.ui.main.event.scrap.recyclerView.ScrappedEventAdapter
import com.emmsale.presentation.ui.main.event.scrap.uistate.ScrappedEventUiState

class ScrappedEventFragment : BaseFragment<FragmentScrappedEventBinding>() {
    override val layoutResId: Int = R.layout.fragment_scrapped_event

    private val viewModel: ScrappedEventViewModel by viewModels { ScrappedEventViewModel.factory }

    private val scrappedEventsAdapter: ScrappedEventAdapter by lazy {
        ScrappedEventAdapter(::showEventDetail)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        setUpScrappedEvents()
    }

    private fun initBinding() {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.rvScrappedEvents.adapter = scrappedEventsAdapter
    }

    private fun setUpScrappedEvents() {
        viewModel.scrappedEvents.observe(viewLifecycleOwner) { scrappedEvents ->
            if (scrappedEvents.isError) {
                requireContext().showToast(getString(R.string.all_data_loading_failed_message))
            } else {
                scrappedEventsAdapter.submitList(scrappedEvents.list)
            }
        }
    }

    private fun showEventDetail(scrappedEventUiState: ScrappedEventUiState) {
        EventDetailActivity.startActivity(requireContext(), scrappedEventUiState.eventId)
    }
}