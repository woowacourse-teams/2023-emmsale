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
import com.emmsale.presentation.ui.main.event.uistate.EventsUiState

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
        initViewModel()
        initEventRecyclerView()
    }

    private fun initViewModel() {
        binding.viewModel = viewModel
    }

    private fun initEventRecyclerView() {
        binding.rvEvents.adapter = eventAdapter
        binding.rvEvents.addItemDecoration(EventRecyclerViewDivider(requireContext()))
    }

    private fun setupEventsObserver() {
        viewModel.events.observe(viewLifecycleOwner) { eventsResult ->
            when (eventsResult) {
                is EventsUiState.Success -> {
                    binding.progressbarLoading.visibility = View.GONE
                    eventAdapter.submitList(eventsResult.events)
                    binding.tvEventsCount.text =
                        getString(R.string.event_count_format, eventsResult.eventSize)
                }

                is EventsUiState.Loading -> binding.progressbarLoading.visibility = View.VISIBLE
                is EventsUiState.Error -> {
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

    companion object {
        const val TAG = "Event"
    }
}
