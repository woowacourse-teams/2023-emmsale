package com.emmsale.presentation.ui.eventdetail.information

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentEventInformationBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegate
import com.emmsale.presentation.common.firebase.analytics.FirebaseAnalyticsDelegateImpl
import com.emmsale.presentation.ui.eventdetail.EventDetailActivity
import com.emmsale.presentation.ui.eventdetail.information.uiState.EventInfoUiEvent

class EventInfoFragment :
    BaseFragment<FragmentEventInformationBinding>(),
    FirebaseAnalyticsDelegate by FirebaseAnalyticsDelegateImpl("event_recruitment") {
    override val layoutResId: Int = R.layout.fragment_event_information

    private val informationUrl: String? by lazy {
        arguments?.getString(INFORMATION_URL_KEY)
    }

    private val imageUrl: String? by lazy {
        arguments?.getString(IMAGE_URL_KEY)
    }

    private val eventId: Long by lazy {
        arguments?.getLong(EVENT_ID_KEY) ?: throw IllegalArgumentException(NOT_EVENT_ID_ERROR)
    }

    private val viewModel: EventInfoViewModel by viewModels { EventInfoViewModel.factory(eventId) }
    private lateinit var eventDetailActivity: EventDetailActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventDetailActivity = context as EventDetailActivity
        registerScreen(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageUrl = imageUrl
    }

    override fun onResume() {
        super.onResume()
        eventDetailActivity.showEventInformation()
        urlButtonClick()
        scarpButtonClick()
        setUpIsScrapped()
        setupUiEvent()
    }

    private fun setUpIsScrapped() {
        viewModel.isScraped.observe(viewLifecycleOwner) { isScrapped ->
            if (isScrapped) {
                setScrapButtonChecked()
                setScrapButtonEnabled(true)
            } else {
                setScrapButtonUnChecked()
                setScrapButtonEnabled(true)
            }
        }
    }

    private fun setScrapButtonChecked() {
        binding.ivEventInformationScrap.setImageResource(R.drawable.ic_all_scrap_checked)
    }

    private fun setScrapButtonUnChecked() {
        binding.ivEventInformationScrap.setImageResource(R.drawable.ic_all_scrap_unchecked)
    }

    private fun setupUiEvent() {
        viewModel.event.observe(viewLifecycleOwner) {
            handleEvent(it)
        }
    }

    private fun handleEvent(event: EventInfoUiEvent?) {
        if (event == null) return
        when (event) {
            EventInfoUiEvent.SCRAP_ERROR -> binding.root.showSnackBar("스크랩 불가")
            EventInfoUiEvent.SCRAP_DELETE_ERROR -> binding.root.showSnackBar("스크랩 삭제 불가")
        }
    }

    private fun urlButtonClick() {
        binding.buttonEventinformationNavigatewebsite.setOnClickListener {
            navigateToUrl()
        }
    }

    private fun scarpButtonClick() {
        binding.ivEventInformationScrap.setOnClickListener {
            if (viewModel.isScraped.value!!) {
                viewModel.deleteScrap()
                setScrapButtonEnabled(false)
            } else {
                viewModel.scrapEvent()
                setScrapButtonEnabled(false)
            }
        }
    }

    private fun setScrapButtonEnabled(state: Boolean) {
        binding.ivEventInformationScrap.isEnabled = state
    }

    private fun navigateToUrl() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(informationUrl))
        startActivity(browserIntent)
    }

    companion object {
        private const val INFORMATION_URL_KEY = "INFORMATION_URL_KEY"
        private const val IMAGE_URL_KEY = "IMAGE_URL_KEY"
        private const val EVENT_ID_KEY = "EVENT_ID_KEY"
        private const val NOT_EVENT_ID_ERROR = "이벤트 아이디를 얻어오지 못했어요"

        fun create(informationUrl: String, imageUrl: String?, eventId: Long): EventInfoFragment {
            val fragment = EventInfoFragment()
            fragment.arguments = Bundle().apply {
                putString(INFORMATION_URL_KEY, informationUrl)
                putString(IMAGE_URL_KEY, imageUrl)
                putLong(EVENT_ID_KEY, eventId)
            }
            return fragment
        }
    }
}
