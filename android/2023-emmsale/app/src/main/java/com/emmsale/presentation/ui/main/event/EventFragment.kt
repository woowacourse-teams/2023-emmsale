package com.emmsale.presentation.ui.main.event

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentEventBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.ui.main.event.uistate.EventsUiSTate

class EventFragment : BaseFragment<FragmentEventBinding>() {
    override val layoutResId: Int = R.layout.fragment_event
    private val viewModel: EventViewModel by viewModels { EventViewModel.factory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchEvents()
        viewModel.events.observe(viewLifecycleOwner) { events ->
            when (events) {
                is EventsUiSTate.Loading -> binding.progressbarLoading.visibility = View.VISIBLE
                is EventsUiSTate.Success -> {
                    binding.progressbarLoading.visibility = View.GONE
                    // 리사이클러뷰에 보여줘야 함
                }

                is EventsUiSTate.Error -> {
                    binding.progressbarLoading.visibility = View.GONE
                    requireContext().showToast("행사 목록을 불러올 수 없어요 \uD83D\uDE22")
                }
            }
        }
    }
}
