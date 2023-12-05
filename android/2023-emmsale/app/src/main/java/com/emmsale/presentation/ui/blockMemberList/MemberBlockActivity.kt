package com.emmsale.presentation.ui.blockMemberList

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.emmsale.R
import com.emmsale.databinding.ActivityMemberBlockBinding
import com.emmsale.presentation.base.NetworkActivity
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.views.ConfirmDialog
import com.emmsale.presentation.ui.blockMemberList.recyclerView.BlockedMemberAdapter
import com.emmsale.presentation.ui.blockMemberList.uiState.BlockedMembersUiEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MemberBlockActivity :
    NetworkActivity<ActivityMemberBlockBinding>(R.layout.activity_member_block) {

    override val viewModel: MemberBlockViewModel by viewModels()
    private val blockedMemberAdapter: BlockedMemberAdapter by lazy { BlockedMemberAdapter(::showUnblockMemberDialog) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        initToolbarMenuClickListener()
        initBlockedMemberRecyclerView()
        setupBlockedMembersObserver()
        setupUiEvent()
    }

    private fun initToolbarMenuClickListener() {
        binding.tbMemberBlock.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.close) finish()
            true
        }
    }

    private fun initBlockedMemberRecyclerView() {
        binding.rvBlockedMembers.adapter = blockedMemberAdapter
    }

    private fun setupBlockedMembersObserver() {
        viewModel.blockedMembers.observe(this) {
            blockedMemberAdapter.submitList(it)
        }
    }

    private fun setupUiEvent() {
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

    private fun showUnblockMemberDialog(blockId: Long) {
        ConfirmDialog(
            context = this,
            title = getString(R.string.memberblock_unblock_member_dialog_title),
            message = getString(R.string.memberblock_unblock_member_dialog_message),
            onPositiveButtonClick = { viewModel.unblockMember(blockId) },
        ).show()
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, MemberBlockActivity::class.java))
        }
    }
}
