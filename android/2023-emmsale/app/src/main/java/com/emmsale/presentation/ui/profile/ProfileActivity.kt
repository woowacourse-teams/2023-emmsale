package com.emmsale.presentation.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityProfileBinding
import com.emmsale.presentation.common.extension.showToast
import com.emmsale.presentation.common.views.CategoryTag
import com.emmsale.presentation.common.views.bottomMenuDialog.BottomMenuDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.MenuItemType
import com.emmsale.presentation.ui.login.LoginActivity
import com.emmsale.presentation.ui.profile.recyclerView.ActivitiesAdapter
import com.emmsale.presentation.ui.profile.recyclerView.ActivitiesAdapterDecoration
import com.emmsale.presentation.ui.profile.uiState.ProfileUiState

class ProfileActivity : AppCompatActivity() {

    private val binding: ActivityProfileBinding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }

    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModel.factory
    }

    private val memberId: Long by lazy {
        intent.getLongExtra(KEY_MEMBER_ID, -1).run {
            if (this == -1L) throw IllegalStateException("프로필을 조회할 회원의 아이디는 1 이상이어야 합니다. 로직을 다시 확인해주세요.")
            this
        }
    }

    private val menuDialog: BottomMenuDialog by lazy {
        BottomMenuDialog(this).apply {
            addMenuItemBelow(getString(R.string.profilemenudialog_block_button_label)) {}
            addMenuItemBelow(
                getString(R.string.profilemenudialog_report_button_label),
                MenuItemType.IMPORTANT,
            ) {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initDataBinding()
        initToolbar()
        setupUiLogic()
        initActivitiesRecyclerView()

        viewModel.fetchMember(memberId)
    }

    private fun initDataBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initToolbar() {
        binding.tbProfileToolbar.setNavigationOnClickListener { finish() }
        binding.tbProfileToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.profile_more -> showMoreMenu()
            }
            true
        }
    }

    private fun showMoreMenu() {
        menuDialog.show()
    }

    private fun setupUiLogic() {
        setupLoginUiLogic()
        setupProfileUiLogic()
    }

    private fun setupLoginUiLogic() {
        viewModel.isLogin.observe(this) {
            handleNotLogin(it)
        }
    }

    private fun setupProfileUiLogic() {
        viewModel.profile.observe(this) {
            handleLoginMember(it)
            handleProfileFetchingError(it)
            handleFields(it)
            handleActivities(it)
        }
    }

    private fun handleLoginMember(profile: ProfileUiState) {
        if (profile.isLoginMember) {
            binding.tbProfileToolbar.menu.clear()
        }
    }

    private fun handleProfileFetchingError(profile: ProfileUiState) {
        if (profile.isFetchingError) {
            showToast(getString(R.string.profile_profile_fetching_error_message))
        }
    }

    private fun handleNotLogin(isLogin: Boolean) {
        if (!isLogin) {
            LoginActivity.startActivity(this)
            finish()
        }
    }

    private fun handleFields(profile: ProfileUiState) {
        binding.cgProfileFields.removeAllViews()

        profile.fields.forEach {
            val tagView = CategoryTag(this).apply { text = it.name }
            binding.cgProfileFields.addView(tagView)
        }
    }

    private fun handleActivities(profile: ProfileUiState) {
        (binding.rvProfileEducations.adapter as ActivitiesAdapter).submitList(
            profile.educations,
        )
        (binding.rvProfileClubs.adapter as ActivitiesAdapter).submitList(profile.clubs)
    }

    private fun initActivitiesRecyclerView() {
        val decoration = ActivitiesAdapterDecoration()
        listOf(
            binding.rvProfileEducations,
            binding.rvProfileClubs,
        ).forEach {
            it.apply {
                adapter = ActivitiesAdapter()
                itemAnimator = null
                addItemDecoration(decoration)
            }
        }
    }

    companion object {
        private const val KEY_MEMBER_ID = "KEY_MEMBER_ID"

        fun startActivity(context: Context, memberId: Long) {
            val intent = Intent(context, ProfileActivity::class.java).apply {
                putExtra(KEY_MEMBER_ID, memberId)
            }

            context.startActivity(intent)
        }
    }
}
