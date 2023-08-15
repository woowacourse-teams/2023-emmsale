package com.emmsale.presentation.ui.eventdetail.information

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.emmsale.R
import com.emmsale.databinding.FragmentEventInformationBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.ui.eventdetail.EventDetailActivity

class EventInfoFragment : BaseFragment<FragmentEventInformationBinding>() {
    override val layoutResId: Int = R.layout.fragment_event_information

    private val informationUrl: String? by lazy {
        arguments?.getString(INFORMATION_URL_KEY)
    }

    private val imageUrl: String? by lazy {
        arguments?.getString(IMAGE_URL_KEY)
    }
    private lateinit var eventDetailActivity: EventDetailActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventDetailActivity = context as EventDetailActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        urlButtonClick()
        scarpButtonClick()
        binding.imageUrl = imageUrl
    }

    override fun onResume() {
        super.onResume()
        eventDetailActivity.showEventInformation()
    }

    private fun urlButtonClick() {
        binding.buttonEventinformationNavigatewebsite.setOnClickListener {
            navigateToUrl()
        }
    }

    private fun scarpButtonClick() {
        binding.ivEventInformationScrap.setOnClickListener {
            binding.ivEventInformationScrap.setImageResource(R.drawable.ic_all_scrap_checked)
        }
    }

    private fun navigateToUrl() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(informationUrl))
        startActivity(browserIntent)
    }

    companion object {
        private const val INFORMATION_URL_KEY = "INFORMATION_URL_KEY"
        private const val IMAGE_URL_KEY = "IMAGE_URL_KEY"

        fun create(informationUrl: String, imageUrl: String?): EventInfoFragment {
            val fragment = EventInfoFragment()
            fragment.arguments = Bundle().apply {
                putString(INFORMATION_URL_KEY, informationUrl)
                putString(IMAGE_URL_KEY, imageUrl)
            }
            return fragment
        }
    }
}
