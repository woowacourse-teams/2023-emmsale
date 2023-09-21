package com.emmsale.presentation.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityProfileBinding
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.views.CategoryTagChip
import com.emmsale.presentation.common.views.InfoDialog
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.BottomMenuDialog
import com.emmsale.presentation.ui.login.LoginActivity
import com.emmsale.presentation.ui.profile.recyclerView.ActivitiesAdapter
import com.emmsale.presentation.ui.profile.recyclerView.ActivitiesAdapterDecoration
import com.emmsale.presentation.ui.profile.uiState.ProfileEvent
import com.emmsale.presentation.ui.profile.uiState.ProfileUiState

class ProfileActivity : AppCompatActivity() {

    private val binding: ActivityProfileBinding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }

    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModel.factory(memberId)
    }

    private val memberId: Long by lazy {
        intent.getLongExtra(KEY_MEMBER_ID, -1).run {
            if (this == -1L) throw IllegalStateException("프로필을 조회할 회원의 아이디는 1 이상이어야 합니다. 로직을 다시 확인해주세요.")
            this
        }
    }

    private val menuDialog: BottomMenuDialog by lazy { BottomMenuDialog(this) }

    private val sendMessageDialog: SendMessageDialog by lazy { SendMessageDialog() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initDataBinding()
        initToolbar()
        setupUiLogic()
        initActivitiesRecyclerView()
    }

    private fun initDataBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initToolbar() {
        binding.tbProfileToolbar.setNavigationOnClickListener { finish() }
        binding.tbProfileToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.more -> showMoreMenu()
            }
            true
        }
    }

    private fun showMoreMenu() {
        menuDialog.resetMenu()
        menuDialog.apply {
            addMenuItemBelow("쪽지 보내기") { onSendMessageButtonClick() }
            if (viewModel.isBlocked()) {
                addMenuItemBelow(getString(R.string.profilemenudialog_unblock_button_label)) { onUnblockButtonClick() }
            } else {
                addMenuItemBelow(getString(R.string.profilemenudialog_block_button_label)) { onBlockButtonClick() }
            }
        }.show()
    }

    private fun onSendMessageButtonClick() {
        sendMessageDialog.show(supportFragmentManager, SendMessageDialog.TAG)
    }

    private fun onBlockButtonClick() {
        WarningDialog(
            context = this@ProfileActivity,
            title = getString(R.string.profilemenudialog_block_button_label),
            message = getString(R.string.profile_block_warning_dialog_message),
            positiveButtonLabel = getString(R.string.all_block),
            negativeButtonLabel = getString(R.string.all_cancel),
            onPositiveButtonClick = { viewModel.blockMember() },
        ).show()
    }

    private fun onUnblockButtonClick() {
        viewModel.unblockMember()
    }

    private fun setupUiLogic() {
        setupLoginUiLogic()
        setupProfileUiLogic()
        setupProfileEventUiLogic()
    }

    private fun setupLoginUiLogic() {
        viewModel.isLogin.observe(this) {
            handleNotLogin(it)
        }
    }

    private fun setupProfileUiLogic() {
        viewModel.profile.observe(this) {
            handleLoginMember(it)
            handleFields(it)
            handleActivities(it)
        }
    }

    private fun setupProfileEventUiLogic() {
        viewModel.event.observe(this) {
            handleEvents(it)
        }
    }

    private fun handleEvents(event: ProfileEvent?) {
        if (event == null) return
        when (event) {
            ProfileEvent.BLOCK_FAIL -> binding.root.showSnackBar(getString(R.string.profile_block_fail_message))
            ProfileEvent.BLOCK_COMPLETE -> InfoDialog(
                context = this,
                title = getString(R.string.profile_block_complete_dialog_title),
                message = getString(R.string.profile_block_complete_dialog_message),
            ).show()

            ProfileEvent.UNBLOCK_FAIL -> binding.root.showSnackBar(getString(R.string.profile_unblock_fail_message))
            ProfileEvent.UNBLOCK_SUCCESS -> binding.root.showSnackBar(getString(R.string.profile_unblock_complete_message))
            ProfileEvent.MESSAGE_SEND_FAIL -> binding.root.showSnackBar(getString(R.string.sendmessagedialog_message_send_fail_message))
            ProfileEvent.MESSAGE_SEND_COMPLETE -> {}
        }
        viewModel.removeEvent()
    }

    private fun handleLoginMember(profile: ProfileUiState) {
        if (profile.isLoginMember) {
            binding.tbProfileToolbar.menu.clear()
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
            val tagView = CategoryTagChip(this).apply { text = it.name }
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
