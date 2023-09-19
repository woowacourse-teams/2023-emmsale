package com.emmsale.presentation.ui.myProfile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentMyProfileBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.views.CategoryTagChip
import com.emmsale.presentation.ui.editMyProfile.EditMyProfileActivity
import com.emmsale.presentation.ui.login.LoginActivity
import com.emmsale.presentation.ui.myProfile.uiState.MyProfileUiState
import com.emmsale.presentation.ui.profile.recyclerView.ActivitiesAdapter
import com.emmsale.presentation.ui.profile.recyclerView.ActivitiesAdapterDecoration

class MyProfileFragment : BaseFragment<FragmentMyProfileBinding>() {
    override val layoutResId: Int = R.layout.fragment_my_profile

    private val viewModel: MyProfileViewModel by viewModels { MyProfileViewModel.factory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDataBinding()
        setupUiLogic()
        initToolbar()
        initActivitiesRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        viewModel.refresh()
    }

    private fun initDataBinding() {
        binding.viewModel = viewModel
    }

    private fun setupUiLogic() {
        setupLoginUiLogic()
        setupMyProfileUiLogic()
    }

    private fun setupLoginUiLogic() {
        viewModel.isLogin.observe(viewLifecycleOwner) {
            handleNotLogin(it)
        }
    }

    private fun setupMyProfileUiLogic() {
        viewModel.myProfile.observe(viewLifecycleOwner) {
            handleFields(it)
            handleActivities(it)
        }
    }

    private fun handleNotLogin(isLogin: Boolean) {
        if (!isLogin) {
            LoginActivity.startActivity(requireContext())
            activity?.finish()
        }
    }

    private fun handleFields(myProfile: MyProfileUiState) {
        binding.cgMyprofileFields.removeAllViews()

        myProfile.fields.forEach {
            val tagView = CategoryTagChip(requireContext()).apply { text = it.name }
            binding.cgMyprofileFields.addView(tagView)
        }
    }

    private fun handleActivities(myProfile: MyProfileUiState) {
        (binding.rvMyprofileEducations.adapter as ActivitiesAdapter).submitList(
            myProfile.educations,
        )
        (binding.rvMyprofileClubs.adapter as ActivitiesAdapter).submitList(myProfile.clubs)
    }

    private fun initToolbar() {
        binding.tbMyprofileToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.myprofile_edit_mode -> EditMyProfileActivity.startActivity(requireContext())
            }
            true
        }
    }

    private fun initActivitiesRecyclerView() {
        val decoration = ActivitiesAdapterDecoration()
        listOf(
            binding.rvMyprofileEducations,
            binding.rvMyprofileClubs,
        ).forEach {
            it.apply {
                adapter = ActivitiesAdapter()
                itemAnimator = null
                addItemDecoration(decoration)
            }
        }
    }

    companion object {
        const val TAG = "TAG_MY_PROFILE"
    }
}
