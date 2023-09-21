package com.emmsale.presentation.ui.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityOnboardingBinding
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.ui.main.MainActivity
import com.emmsale.presentation.ui.onboarding.uiState.MemberSavingUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {
    private val binding: ActivityOnboardingBinding by lazy {
        ActivityOnboardingBinding.inflate(layoutInflater)
    }
    private val viewModel: OnboardingViewModel by viewModels()
    private val fragmentStateAdapter: OnboardingFragmentStateAdapter by lazy {
        OnboardingFragmentStateAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initFragmentStateAdapter()
        initBackPressedDispatcher()
        setupActivitiesUiState()
    }

    private fun setupActivitiesUiState() {
        viewModel.activities.observe(this) { activities ->
            when (activities.memberSavingUiState) {
                is MemberSavingUiState.None -> Unit
                is MemberSavingUiState.Success -> navigateToMain()
                is MemberSavingUiState.Failed -> showMemberUpdateFailed()
            }
        }
    }

    private fun initFragmentStateAdapter() {
        binding.vpOnboarding.adapter = fragmentStateAdapter
    }

    private fun initBackPressedDispatcher() {
        onBackPressedDispatcher.addCallback(this, OnboardingOnBackPressedCallback())
    }

    private fun navigateToMain() {
        binding.progressbarLoading.visibility = View.GONE
        MainActivity.startActivity(this)
        finish()
    }

    private fun showMemberUpdateFailed() {
        binding.progressbarLoading.visibility = View.GONE
        binding.root.showSnackBar(getString(R.string.onboarding_member_update_failed_message))
    }

    fun navigateToNextPage() {
        val currentPage = binding.vpOnboarding.currentItem
        val lastPage = fragmentStateAdapter.itemCount - 1
        when {
            currentPage < lastPage -> binding.vpOnboarding.currentItem += 1
            currentPage == lastPage -> viewModel.updateMember()
        }
    }

    fun navigateToPrevPage() {
        when {
            binding.vpOnboarding.currentItem == 0 -> finish()
            binding.vpOnboarding.currentItem > 0 -> binding.vpOnboarding.currentItem -= 1
        }
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, OnboardingActivity::class.java)
            context.startActivity(intent)
        }
    }

    inner class OnboardingOnBackPressedCallback : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            navigateToPrevPage()
        }
    }
}
