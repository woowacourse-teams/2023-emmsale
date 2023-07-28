package com.emmsale.presentation.ui.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.databinding.ActivityOnboardingBinding
import com.emmsale.presentation.ui.onboarding.uistate.MemberUiState

class OnboardingActivity : AppCompatActivity() {
    private val binding: ActivityOnboardingBinding by lazy {
        ActivityOnboardingBinding.inflate(layoutInflater)
    }
    private val viewModel: OnboardingViewModel by viewModels { OnboardingViewModel.factory }
    private val fragmentStateAdapter: OnboardingFragmentStateAdapter by lazy {
        OnboardingFragmentStateAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initFragmentStateAdapter()
        initBackPressedDispatcher()
        setupMemberUiState()
    }

    private fun setupMemberUiState() {
        viewModel.memberUiState.observe(this) { memberState ->
            when (memberState) {
                is MemberUiState.Success -> navigateToMain()
                is MemberUiState.Failed -> showMemberUpdateFailed()
                is MemberUiState.Loading -> binding.progressbarLoading.visibility = View.VISIBLE
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
        // MainActivity.startActivity(this)
        // finish()
    }

    private fun showMemberUpdateFailed() {
        binding.progressbarLoading.visibility = View.GONE
        Toast.makeText(this, "회원정보 업데이트 실패", Toast.LENGTH_SHORT).show()
    }

    fun navigateToNextPage() {
        val currentPage = binding.vpOnboarding.currentItem
        val lastPage = fragmentStateAdapter.itemCount - 1
        when {
            currentPage < lastPage -> binding.vpOnboarding.currentItem += 1
            currentPage == lastPage -> viewModel.updateMember()
        }
    }

    private fun navigateToPrevPage() {
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
