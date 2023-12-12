package com.emmsale.presentation.ui.blockMemberList

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.emmsale.R
import com.emmsale.databinding.ActivityBlockedMembersBinding
import com.emmsale.presentation.base.NetworkActivity
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.views.ConfirmDialog
import com.emmsale.presentation.ui.blockMemberList.recyclerView.BlockedMemberAdapter
import com.emmsale.presentation.ui.blockMemberList.uiState.BlockedMembersUiEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BlockedMembersActivity :
    NetworkActivity<ActivityBlockedMembersBinding>(R.layout.activity_blocked_members) {

    override val viewModel: BlockedMembersViewModel by viewModels()
    private val blockedMemberAdapter: BlockedMemberAdapter by lazy { BlockedMemberAdapter(::showUnblockMemberDialog) }

    private fun showUnblockMemberDialog(blockId: Long) {
        ConfirmDialog(
            context = this,
            title = getString(R.string.memberblock_unblock_member_dialog_title),
            message = getString(R.string.memberblock_unblock_member_dialog_message),
            onPositiveButtonClick = { viewModel.unblockMember(blockId) },
        ).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupDateBinding()
        setupToolbar()
        setupBlockedMembersRecyclerView()

        observeBlockedMembers()
        observeUiEvent()
    }

    private fun setupDateBinding() {
        binding.viewModel = viewModel
    }

    private fun setupToolbar() {
        binding.tbMemberBlock.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.close) finish()
            true
        }
    }

    private fun setupBlockedMembersRecyclerView() {
        binding.rvBlockedMembers.adapter = blockedMemberAdapter
    }

    private fun observeBlockedMembers() {
        viewModel.blockedMembers.observe(this) {
            blockedMemberAdapter.submitList(it)
        }
    }

    private fun observeUiEvent() {
        viewModel.uiEvent.observe(this) {
            handleEvent(it)
        }
    }

    private fun handleEvent(uiEvent: BlockedMembersUiEvent) {
        when (uiEvent) {
            BlockedMembersUiEvent.DeleteFail -> binding.root.showSnackBar(R.string.memberblock_unblock_member_failed_message)
            BlockedMembersUiEvent.FetchFail -> binding.root.showSnackBar(R.string.memberblock_loading_blocked_members_failed_message)
        }
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, BlockedMembersActivity::class.java))
        }
    }
}
