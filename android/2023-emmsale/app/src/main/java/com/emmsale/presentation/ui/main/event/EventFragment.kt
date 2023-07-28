package com.emmsale.presentation.ui.main.event

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentEventBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.ui.main.event.recyclerview.EventRecyclerViewAdapter
import com.emmsale.presentation.ui.main.event.uistate.EventUiState
import com.emmsale.presentation.ui.main.event.uistate.EventsUiSTate

class EventFragment : BaseFragment<FragmentEventBinding>() {
    override val layoutResId: Int = R.layout.fragment_event
    private val viewModel: EventViewModel by viewModels { EventViewModel.factory }
    private val eventAdapter: EventRecyclerViewAdapter by lazy { EventRecyclerViewAdapter(::navigateToEventDetail) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setupEventsObserver()
        viewModel.fetchEvents()
    }

    private fun initView() {
        binding.viewModel = viewModel
        binding.rvEvents.adapter = eventAdapter
    }

    private fun setupEventsObserver() {
        viewModel.events.observe(viewLifecycleOwner) { eventsResult ->
            when (eventsResult) {
                is EventsUiSTate.Success -> {
                    binding.progressbarLoading.visibility = View.GONE
                    eventAdapter.submitList(eventsResult.events)
                }

                is EventsUiSTate.Loading -> binding.progressbarLoading.visibility = View.VISIBLE
                is EventsUiSTate.Error -> {
                    binding.progressbarLoading.visibility = View.GONE
                    requireContext().showToast("행사 목록을 불러올 수 없어요 \uD83D\uDE22")
                }
            }
        }
    }

    private fun navigateToEventDetail(event: EventUiState) {
        Log.d("buna", event.toString())
        // EventDetail.startActivity(event)
    }
}
