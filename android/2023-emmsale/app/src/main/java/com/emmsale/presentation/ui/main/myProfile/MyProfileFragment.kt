package com.emmsale.presentation.ui.main.myProfile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
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
        initActivitiesRecyclerView(binding.rvMyprofileEducations)
        initActivitiesRecyclerView(binding.rvMyprofileClubs)
        initActivitiesRecyclerView(binding.rvMyprofileEvents)
    }

    private fun initJobsRecyclerView() {
        binding.rvMyprofileJobs.apply {
            adapter = JobsAdapter()
            itemAnimator = null
        }
    }

    private fun initActivitiesRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply {
            adapter = ActivitiesAdapter()
            itemAnimator = null
            addItemDecoration(ActivitiesAdapterDecoration.getInstance())
        }
    }
}
