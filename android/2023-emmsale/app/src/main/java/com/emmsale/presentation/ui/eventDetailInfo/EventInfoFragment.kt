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
    BaseFragment<FragmentEventInformationBinding>(),
    FirebaseAnalyticsDelegate by FirebaseAnalyticsDelegateImpl("event_recruitment") {
    override val layoutResId: Int = R.layout.fragment_event_information
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
        viewModel.eventDetail.observe(viewLifecycleOwner) { eventDetailUiState ->
            val imageUrls = eventDetailUiState.eventDetail?.imageUrls ?: return@observe
            binding.rvEventInfoImages.setHasFixedSize(true)
            binding.rvEventInfoImages.adapter = EventInfoImageAdapter(imageUrls)
        }
    }

    companion object {
        fun create(): EventInfoFragment {
            return EventInfoFragment()
        }
    }
}
