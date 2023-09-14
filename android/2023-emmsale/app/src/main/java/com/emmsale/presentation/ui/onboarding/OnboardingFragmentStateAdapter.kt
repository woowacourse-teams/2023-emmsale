package com.emmsale.presentation.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingFragmentStateAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = ONBOARDING_FRAGMENT_SIZE

    override fun createFragment(position: Int): Fragment = when (position) {
        NAME_FRAGMENT_POSITION -> OnboardingNameFragment()
        FIELD_FRAGMENT_POSITION -> OnboardingFieldFragment()
        FIELD_EDUCATION_POSITION -> OnboardingEducationFragment()
        FIELD_CLUB_POSITION -> OnboardingClubFragment()
        else -> throw IllegalArgumentException("올바르지 않은 fragment position 입니다.")
    }

    companion object {
        private const val ONBOARDING_FRAGMENT_SIZE = 4
        private const val NAME_FRAGMENT_POSITION = 0
        private const val FIELD_FRAGMENT_POSITION = 1
        private const val FIELD_EDUCATION_POSITION = 2
        private const val FIELD_CLUB_POSITION = 3
    }
}
