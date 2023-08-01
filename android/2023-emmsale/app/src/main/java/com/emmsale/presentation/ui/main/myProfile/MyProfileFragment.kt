package com.emmsale.presentation.ui.main.myProfile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentMyProfileBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.ui.login.LoginActivity
import com.emmsale.presentation.ui.main.myProfile.adapter.ActivitiesAdapter
import com.emmsale.presentation.ui.main.myProfile.adapter.JobsAdapter
import com.emmsale.presentation.ui.main.myProfile.itemDecoration.ActivitiesAdapterDecoration
import com.emmsale.presentation.ui.main.myProfile.uiState.MyProfileScreenUiState

class MyProfileFragment : BaseFragment<FragmentMyProfileBinding>() {
    override val layoutResId: Int = R.layout.fragment_my_profile

    private val viewModel: MyProfileViewModel by viewModels {
        MyProfileViewModel.factory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDataBinding()
        setupUiLogic()
        initRecyclerViews()

        viewModel.fetchMember()
    }

    private fun initDataBinding() {
        binding.viewModel = viewModel
    }

    private fun setupUiLogic() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            handleError(it)
            handleNotLogin(it)
        }
    }

    private fun handleError(myProfileScreenUiState: MyProfileScreenUiState) {
        if (myProfileScreenUiState.isError) {
            context?.showToast(myProfileScreenUiState.errorMessage)
        }
    }

    private fun handleNotLogin(myProfileScreenUiState: MyProfileScreenUiState) {
        if (myProfileScreenUiState.isNotLogin) {
            LoginActivity.startActivity(requireContext())
            activity?.finish()
        }
    }

    private fun initRecyclerViews() {
        initJobsRecyclerView()
        initActivitiesRecyclerView()
    }

    private fun initJobsRecyclerView() {
        binding.rvMyprofileJobs.apply {
            adapter = JobsAdapter()
            itemAnimator = null
        }
    }

    private fun initActivitiesRecyclerView() {
        listOf(
            binding.rvMyprofileEducations,
            binding.rvMyprofileClubs,
            binding.rvMyprofileEvents
        ).forEach {
            it.apply {
                adapter = ActivitiesAdapter()
                itemAnimator = null
                addItemDecoration(ActivitiesAdapterDecoration.getInstance())
            }
        }
    }

    companion object {
        const val TAG = "MyProfile"
    }
}
