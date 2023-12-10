package com.emmsale.presentation.ui.eventDetailInfo

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentEventInformationBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegate
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegateImpl
import com.emmsale.presentation.ui.eventDetail.EventDetailViewModel
import com.emmsale.presentation.ui.eventDetailInfo.recyclerView.EventInfoImageAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventInfoFragment :
    BaseFragment<FragmentEventInformationBinding>(R.layout.fragment_event_information),
    FirebaseAnalyticsDelegate by FirebaseAnalyticsDelegateImpl("event_recruitment") {

    private val viewModel: EventDetailViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        registerScreen(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        setUpInformationUrls()
    }

    private fun setUpInformationUrls() {
        viewModel.event.observe(viewLifecycleOwner) { event ->
            binding.rvEventInfoImages.setHasFixedSize(true)
            binding.rvEventInfoImages.adapter = EventInfoImageAdapter(event.detailImageUrls)
        }
    }

    companion object {
        fun create(): EventInfoFragment {
            return EventInfoFragment()
        }
    }
}
