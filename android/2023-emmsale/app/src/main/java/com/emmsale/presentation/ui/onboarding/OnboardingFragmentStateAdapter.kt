package com.emmsale.presentation.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingFragmentStateAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    private val fragments: List<Fragment> = listOf(
        OnboardingNameFragment(),
        OnboardingEducationCareerFragment(),
        OnboardingClubCareerFragment(),
        OnboardingJobCareerFragment(),
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
