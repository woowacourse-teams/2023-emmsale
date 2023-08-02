package com.emmsale.presentation.ui.eventdetail.information

import android.os.Bundle
import com.emmsale.R
import com.emmsale.databinding.FragmentEventInformationBinding
import com.emmsale.presentation.base.fragment.BaseFragment

class EventInfoFragment() : BaseFragment<FragmentEventInformationBinding>() {
    override val layoutResId: Int = R.layout.fragment_event_information

    companion object {
        private const val INFORMATION_URL_KEY = "INFORMATION_URL_KEY"
        private const val IMAGE_URL_KEY = "IMAGE_URL_KEY"

        fun create(informationUrl: String, imageUrl: String): EventInfoFragment {
            val fragment = EventInfoFragment()
            fragment.arguments = Bundle().apply {
                putString(INFORMATION_URL_KEY, informationUrl)
                putString(IMAGE_URL_KEY, imageUrl)
            }
            return fragment
        }
    }
}
