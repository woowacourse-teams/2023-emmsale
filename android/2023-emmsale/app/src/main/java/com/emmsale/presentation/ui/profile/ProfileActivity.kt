package com.emmsale.presentation.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.emmsale.R
import com.emmsale.databinding.ActivityProfileBinding
import com.emmsale.presentation.base.NetworkActivity
import com.emmsale.presentation.common.extension.dp
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.recyclerView.IntervalItemDecoration
import com.emmsale.presentation.common.views.CategoryTagChip
import com.emmsale.presentation.common.views.InfoDialog
import com.emmsale.presentation.common.views.WarningDialog
import com.emmsale.presentation.common.views.bottomMenuDialog.BottomMenuDialog
import com.emmsale.presentation.ui.messageList.MessageListActivity
import com.emmsale.presentation.ui.profile.ProfileViewModel.Companion.KEY_MEMBER_ID
import com.emmsale.presentation.ui.profile.recyclerView.ActivitiesAdapter
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

        setupDataBinding()
        setupToolbar()
        setupActivitiesRecyclerView()

        observeProfile()
        observeUiEvent()
    }

    private fun setupDataBinding() {
        binding.viewModel = viewModel
    }

    private fun setupToolbar() {
        binding.tbProfileToolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
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
                addMenuItemBelow(getString(R.string.profilemenudialog_unblock_button_label)) { viewModel.unblockMember() }
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

    private fun setupActivitiesRecyclerView() {
        val decoration = IntervalItemDecoration(height = 13.dp)
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

    private fun observeProfile() {
        viewModel.profile.observe(this) {
            handleLoginMember(it)
            handleFields(it)
            handleActivities(it)
        }
    }

    private fun handleLoginMember(profile: ProfileUiState) {
        if (profile.isLoginMember) {
            binding.tbProfileToolbar.menu.clear()
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

    private fun observeUiEvent() {
        viewModel.uiEvent.observe(this, ::handleUiEvent)
    }

    private fun handleUiEvent(uiEvent: ProfileUiEvent) {
        when (uiEvent) {
            ProfileUiEvent.BlockComplete -> InfoDialog(
                context = this,
                title = getString(R.string.profile_block_complete_dialog_title),
                message = getString(R.string.profile_block_complete_dialog_message),
            ).show()

            ProfileUiEvent.BlockFail -> binding.root.showSnackBar(getString(R.string.profile_block_fail_message))
            is ProfileUiEvent.MessageSendComplete -> {
                MessageListActivity.startActivity(
                    this,
                    uiEvent.roomId,
                    uiEvent.otherId,
                    true,
                )
                sendMessageDialog.clearText()
                sendMessageDialog.dismiss()
            }

            ProfileUiEvent.MessageSendFail -> binding.root.showSnackBar(getString(R.string.sendmessagedialog_message_send_fail_message))
            ProfileUiEvent.UnblockFail -> binding.root.showSnackBar(getString(R.string.profile_unblock_fail_message))
            ProfileUiEvent.UnblockSuccess -> binding.root.showSnackBar(getString(R.string.profile_unblock_complete_message))
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
