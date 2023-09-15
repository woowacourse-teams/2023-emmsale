package com.emmsale.presentation.ui.eventDetailInfo

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentEventInformationBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegate
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegateImpl
import com.emmsale.presentation.ui.eventDetail.EventDetailViewModel
import com.emmsale.presentation.ui.eventDetailInfo.uiState.EventInfoUiEvent

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
    }

    // private fun setUpIsScrapped() {
    //     viewModel.isScraped.observe(viewLifecycleOwner) { isScrapped ->
    //         if (isScrapped) {
    //             setScrapButtonChecked()
    //             setScrapButtonEnabled(true)
    //         } else {
    //             setScrapButtonUnChecked()
    //             setScrapButtonEnabled(true)
    //         }
    //     }
    // }

    // private fun setScrapButtonChecked() {
    //     binding.ivEventInformationScrap.setImageResource(R.drawable.ic_all_scrap_checked)
    // }
    //
    // private fun setScrapButtonUnChecked() {
    //     binding.ivEventInformationScrap.setImageResource(R.drawable.ic_all_scrap_unchecked)
    // }

    private fun handleEvent(event: EventInfoUiEvent?) {
        if (event == null) return
        when (event) {
            EventInfoUiEvent.SCRAP_ERROR -> binding.root.showSnackBar("스크랩 불가")
            EventInfoUiEvent.SCRAP_DELETE_ERROR -> binding.root.showSnackBar("스크랩 삭제 불가")
        }
    }

    // private fun urlButtonClick() {
    //     binding.buttonEventinformationNavigatewebsite.setOnClickListener {
    //         navigateToUrl()
    //     }
    // }

    // private fun scarpButtonClick() {
    //     binding.ivEventInformationScrap.setOnClickListener {
    //         if (viewModel.isScraped.value!!) {
    //             viewModel.deleteScrap()
    //             setScrapButtonEnabled(false)
    //         } else {
    //             viewModel.scrapEvent()
    //             setScrapButtonEnabled(false)
    //         }
    //     }
    // }

    // private fun setScrapButtonEnabled(state: Boolean) {
    //     binding.ivEventInformationScrap.isEnabled = state
    // }

    companion object {
        fun create(): EventInfoFragment {
            return EventInfoFragment()
        }
    }
}
