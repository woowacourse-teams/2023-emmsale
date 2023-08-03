package com.emmsale.presentation.ui.eventdetail.comment

import android.os.Bundle
import com.emmsale.R
import com.emmsale.databinding.FragmentEventCommentBinding
import com.emmsale.presentation.base.fragment.BaseFragment

class EventCommentFragment() : BaseFragment<FragmentEventCommentBinding>() {
    override val layoutResId: Int = R.layout.fragment_event_comment

    companion object {
        private const val EVENT_ID_KEY = "EVENT_ID_KEY"

        fun create(eventId: Long): EventCommentFragment {
            val fragment = EventCommentFragment()
            fragment.arguments = Bundle().apply {
                putLong(EVENT_ID_KEY, eventId)
            }
            return fragment
        }
    }
}
