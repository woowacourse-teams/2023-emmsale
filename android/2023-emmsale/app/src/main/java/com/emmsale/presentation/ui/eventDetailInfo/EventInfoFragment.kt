package com.emmsale.presentation.ui.eventDetailInfo

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.data.model.PaymentType
import com.emmsale.databinding.FragmentEventInformationBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegate
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegateImpl
import com.emmsale.presentation.ui.eventDetail.EventDetailViewModel
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

        setUpEventDetail()
    }

    private fun setUpEventDetail() {
        viewModel.eventDetail.observe(viewLifecycleOwner) {
            binding.tvEventInfoCost.text = when (it.eventDetail.paymentType) {
                PaymentType.PAID -> getString(R.string.all_paid)
                PaymentType.FREE -> getString(R.string.all_free)
                PaymentType.PAID_OR_FREE -> getString(R.string.all_paid_free)
            }
        }
    }

    companion object {
        fun create(): EventInfoFragment {
            return EventInfoFragment()
        }
    }
}
