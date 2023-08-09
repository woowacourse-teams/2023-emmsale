package com.emmsale.presentation.ui.main.myProfile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentMyProfileBinding
import com.emmsale.presentation.base.fragment.BaseFragment
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.views.CategoryTag
import com.emmsale.presentation.ui.login.LoginActivity
import com.emmsale.presentation.ui.main.myProfile.recyclerView.ActivitiesAdapter
import com.emmsale.presentation.ui.main.myProfile.recyclerView.ActivitiesAdapterDecoration
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
        initActivitiesRecyclerView()

        viewModel.fetchMember()
    }

    private fun initDataBinding() {
        binding.viewModel = viewModel
    }

    private fun setupUiLogic() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            handleError(it)
            handleNotLogin(it)
            handleCategories(it)
            handleActivities(it)
        }
    }

    private fun handleError(myProfileScreenUiState: MyProfileScreenUiState) {
        if (myProfileScreenUiState.isError) {
            context?.showToast(myProfileScreenUiState.errorMessage)
            viewModel.onErrorMessageViewed()
        }
    }

    private fun handleNotLogin(myProfileScreenUiState: MyProfileScreenUiState) {
        if (myProfileScreenUiState.isNotLogin) {
            LoginActivity.startActivity(requireContext())
            activity?.finish()
        }
    }

    private fun handleCategories(myProfileScreenUiState: MyProfileScreenUiState) {
        binding.cgMyprofileCategories.removeAllViews()

        myProfileScreenUiState.fields.forEach {
            val tagView = CategoryTag(requireContext()).apply { text = it.name }
            binding.cgMyprofileCategories.addView(tagView)
        }
    }

    private fun handleActivities(myProfileScreenUiState: MyProfileScreenUiState) {
        (binding.rvMyprofileEducations.adapter as ActivitiesAdapter).submitList(
            myProfileScreenUiState.educations,
        )
        (binding.rvMyprofileClubs.adapter as ActivitiesAdapter).submitList(myProfileScreenUiState.clubs)
    }

    private fun initActivitiesRecyclerView() {
        listOf(
            binding.rvMyprofileEducations,
            binding.rvMyprofileClubs,
        ).forEach {
            val decoration = ActivitiesAdapterDecoration()
            it.apply {
                adapter = ActivitiesAdapter()
                itemAnimator = null
                addItemDecoration(decoration)
            }
        }
    }

    companion object {
        const val TAG = "MyProfile"
    }
}
