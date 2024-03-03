package com.emmsale.presentation.ui.myProfile

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentMyProfileBinding
import com.emmsale.model.Activity
import com.emmsale.presentation.base.NetworkFragment
import com.emmsale.presentation.common.extension.dp
import com.emmsale.presentation.common.recyclerView.IntervalItemDecoration
import com.emmsale.presentation.common.views.CategoryTagChip
import com.emmsale.presentation.ui.editMyProfile.EditMyProfileActivity
import com.emmsale.presentation.ui.profile.recyclerView.ActivitiesAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyProfileFragment : NetworkFragment<FragmentMyProfileBinding>(R.layout.fragment_my_profile) {

    override val viewModel: MyProfileViewModel by viewModels()

    private val editMyProfileActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != RESULT_OK) return@registerForActivityResult
            viewModel.refresh()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDataBinding()
        setupToolbar()
        setupActivitiesRecyclerView()

        observeProfile()
    }

    private fun setupDataBinding() {
        binding.viewModel = viewModel
    }

    private fun setupToolbar() {
        binding.tbMyprofileToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.myprofile_edit_mode -> {
                    val intent = EditMyProfileActivity.getIntent(requireContext())
                    editMyProfileActivityLauncher.launch(intent)
                }
            }
            true
        }
    }

    private fun setupActivitiesRecyclerView() {
        val decoration = IntervalItemDecoration(height = 13.dp)
        listOf(
            binding.layoutProfile.rvProfileEducations,
            binding.layoutProfile.rvProfileClubs,
        ).forEach {
            it.apply {
                adapter = ActivitiesAdapter()
                itemAnimator = null
                addItemDecoration(decoration)
            }
        }
    }

    private fun observeProfile() {
        viewModel.profile.observe(viewLifecycleOwner) { member ->
            handleFields(member.fields)
            handleEducations(member.educations)
            handleClubs(member.clubs)
        }
    }

    private fun handleFields(fields: List<Activity>) {
        binding.layoutProfile.cgProfileFields.removeAllViews()

        fields.forEach {
            val tagView = CategoryTagChip(requireContext()).apply { text = it.name }
            binding.layoutProfile.cgProfileFields.addView(tagView)
        }
    }

    private fun handleEducations(educations: List<Activity>) {
        (binding.layoutProfile.rvProfileEducations.adapter as ActivitiesAdapter).submitList(educations)
    }

    private fun handleClubs(clubs: List<Activity>) {
        (binding.layoutProfile.rvProfileClubs.adapter as ActivitiesAdapter).submitList(clubs)
    }

    companion object {
        const val TAG = "TAG_MY_PROFILE"
    }
}
