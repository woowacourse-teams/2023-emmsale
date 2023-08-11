package com.emmsale.presentation.ui.notificationBox

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.emmsale.presentation.ui.notificationBox.otherNotification.PrimaryNotificationFragment
import com.emmsale.presentation.ui.notificationBox.recruitmentNotification.RecruitmentNotificationFragment

class NotificationBoxFragmentStateAdapter(
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = NOTIFICATION_BOX_TAB_COUNT

    override fun createFragment(position: Int): Fragment = when (position) {
        RECRUITMENT_NOTIFICATION_TAB_POSITION -> RecruitmentNotificationFragment()
        PRIMARY_NOTIFICATION_TAB_POSITION -> PrimaryNotificationFragment()
        else -> throw IllegalArgumentException("Invalid position: $position")
    }

    companion object {
        private const val NOTIFICATION_BOX_TAB_COUNT = 2
        private const val RECRUITMENT_NOTIFICATION_TAB_POSITION = 0
        private const val PRIMARY_NOTIFICATION_TAB_POSITION = 1
    }
}