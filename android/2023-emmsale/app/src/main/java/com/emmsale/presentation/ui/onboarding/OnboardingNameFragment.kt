package com.emmsale.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentOnboardingNameBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.common.viewModelFactory

class OnboardingNameFragment : BaseFragment<FragmentOnboardingNameBinding>() {
    override val viewModel: OnboardingViewModel by activityViewModels { viewModelFactory }
    override val layoutResId: Int = R.layout.fragment_onboarding_name

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
    }
}
