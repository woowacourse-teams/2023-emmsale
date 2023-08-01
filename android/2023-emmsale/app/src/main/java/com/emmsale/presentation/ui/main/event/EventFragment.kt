package com.emmsale.presentation.ui.main.event

import com.emmsale.R
import com.emmsale.databinding.FragmentEventBinding
import com.emmsale.presentation.base.fragment.BaseFragment

class EventFragment : BaseFragment<FragmentEventBinding>() {
    override val layoutResId: Int = R.layout.fragment_event

    companion object {
        val TAG: String = EventFragment::class.java.simpleName
    }
}
