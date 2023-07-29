package com.emmsale.presentation.ui.main.myProfile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentMyProfileBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.ui.main.myProfile.adapter.ActivitiesAdapter
import com.emmsale.presentation.ui.main.myProfile.adapter.JobsAdapter
import com.emmsale.presentation.ui.main.myProfile.itemDecoration.ActivitiesAdapterDecoration

class MyProfileFragment : BaseFragment<FragmentMyProfileBinding>() {
    override val layoutResId: Int = R.layout.fragment_my_profile

    private val viewModel: MyProfileViewModel by viewModels {
        MyProfileViewModel.factory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDataBinding()
        initUiLogic()
        initRecyclerViews()

        viewModel.fetchMember()
    }

    private fun initDataBinding() {
        binding.viewModel = viewModel
    }

    private fun initUiLogic() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            if (it.isError) {
                context?.showToast(it.errorMessage)
            }
        }
    }

    private fun initRecyclerViews() {
        initJobsRecyclerView()
        initEducationsRecyclerView()
        initClubsRecyclerView()
        initEventsRecyclerView()
    }

    private fun initJobsRecyclerView() {
        binding.rvMyprofileJobs.apply {
            adapter = JobsAdapter()
            itemAnimator = null
        }
    }

    private fun initEducationsRecyclerView() {
        binding.rvMyprofileEducations.apply {
            adapter = ActivitiesAdapter()
            itemAnimator = null
            addItemDecoration(ActivitiesAdapterDecoration.getInstance())
        }
    }

    private fun initClubsRecyclerView() {
        binding.rvMyprofileClubs.apply {
            adapter = ActivitiesAdapter()
            itemAnimator = null
            addItemDecoration(ActivitiesAdapterDecoration.getInstance())
        }
    }

    private fun initEventsRecyclerView() {
        binding.rvMyprofileEvents.apply {
            adapter = ActivitiesAdapter()
            itemAnimator = null
            addItemDecoration(ActivitiesAdapterDecoration.getInstance())
        }
    }
}
