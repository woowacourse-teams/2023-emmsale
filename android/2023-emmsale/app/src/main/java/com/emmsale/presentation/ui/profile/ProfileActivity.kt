package com.emmsale.presentation.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.emmsale.R
import com.emmsale.databinding.ActivityProfileBinding
import com.emmsale.presentation.base.NetworkActivity
import com.emmsale.presentation.common.UiEvent
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.views.CategoryTagChip
import com.emmsale.presentation.common.views.InfoDialog
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.BottomMenuDialog
import com.emmsale.presentation.ui.login.LoginActivity
import com.emmsale.presentation.ui.messageList.MessageListActivity
import com.emmsale.presentation.ui.profile.ProfileViewModel.Companion.KEY_MEMBER_ID
import com.emmsale.presentation.ui.profile.recyclerView.ActivitiesAdapter
import com.emmsale.presentation.ui.profile.recyclerView.ActivitiesAdapterDecoration
import com.emmsale.presentation.ui.profile.uiState.ProfileUiEvent
import com.emmsale.presentation.ui.profile.uiState.ProfileUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : NetworkActivity<ActivityProfileBinding>(R.layout.activity_profile) {

    override val viewModel: ProfileViewModel by viewModels()

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
        viewModel.uiEvent.observe(this) {
            handleUiEvent(it)
        }
    }

    private fun handleUiEvent(event: UiEvent<ProfileUiEvent>) {
        val content = event.getContentIfNotHandled() ?: return
        when (content) {
            ProfileUiEvent.BlockComplete -> InfoDialog(
                context = this,
                title = getString(R.string.profile_block_complete_dialog_title),
                message = getString(R.string.profile_block_complete_dialog_message),
            ).show()

            ProfileUiEvent.BlockFail -> binding.root.showSnackBar(getString(R.string.profile_block_fail_message))
            is ProfileUiEvent.MessageSendComplete -> {
                MessageListActivity.startActivity(
                    this,
                    content.roomId,
                    content.otherId,
                )
                sendMessageDialog.clearText()
                sendMessageDialog.dismiss()
            }

            ProfileUiEvent.MessageSendFail -> binding.root.showSnackBar(getString(R.string.sendmessagedialog_message_send_fail_message))
            ProfileUiEvent.None -> {}
            ProfileUiEvent.UnblockFail -> binding.root.showSnackBar(getString(R.string.profile_unblock_fail_message))
            ProfileUiEvent.UnblockSuccess -> binding.root.showSnackBar(getString(R.string.profile_unblock_complete_message))
        }
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

        profile.member.fields.forEach {
            val tagView = CategoryTagChip(this).apply { text = it.name }
            binding.cgProfileFields.addView(tagView)
        }
    }

    private fun handleActivities(profile: ProfileUiState) {
        (binding.rvProfileEducations.adapter as ActivitiesAdapter).submitList(
            profile.member.educations,
        )

        (binding.rvProfileClubs.adapter as ActivitiesAdapter).submitList(
            profile.member.clubs,
        )
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
        fun startActivity(context: Context, memberId: Long) {
            val intent = Intent(context, ProfileActivity::class.java)
                .putExtra(KEY_MEMBER_ID, memberId)
            context.startActivity(intent)
        }
    }
}
