package com.emmsale.presentation.ui.eventDetailInfo

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentEventInformationBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegate
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegateImpl
import com.emmsale.presentation.common.views.InfoDialog
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

        setupDataBinding()
        setUpInformationUrls()
    }

    private fun setupDataBinding() {
        binding.vm = viewModel
        binding.onMapSearchButtonClick = ::navigateToMapApp
    }

    private fun navigateToMapApp(location: String) {
        val uri = Uri.parse("geo:0,0?q=$location")
        val intent = Intent(Intent.ACTION_VIEW, uri)

        runCatching { startActivity(intent) }
            .onFailure {
                if (it is ActivityNotFoundException) showNotFoundMapAppDialog()
            }
    }

    private fun showNotFoundMapAppDialog() {
        InfoDialog(
            context = requireContext(),
            title = getString(R.string.eventInfo_not_found_map_app_dialog_title),
            message = getString(R.string.eventInfo_not_found_map_app_dialog_message),
            buttonLabel = getString(R.string.all_okay),
        ).show()
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
