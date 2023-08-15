package com.emmsale.presentation.ui.setting.block

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityMemberBlockBinding
import com.emmsale.presentation.common.extension.showSnackbar
import com.emmsale.presentation.common.views.ConfirmDialog
import com.emmsale.presentation.ui.setting.block.recyclerview.BlockedMemberAdapter
import com.emmsale.presentation.ui.setting.block.uistate.BlockedMembersUiState

class MemberBlockActivity : AppCompatActivity() {
    private val binding: ActivityMemberBlockBinding by lazy {
        ActivityMemberBlockBinding.inflate(layoutInflater)
    }
    private val viewModel: MemberBlockViewModel by viewModels { MemberBlockViewModel.factory }
    private val blockedMemberAdapter: BlockedMemberAdapter by lazy { BlockedMemberAdapter(::showUnblockMemberDialog) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        initBlockedMemberRecyclerView()
        setupBlockedMembersObserver()
    }

    private fun initBlockedMemberRecyclerView() {
        binding.rvBlockedMembers.adapter = blockedMemberAdapter
    }

    private fun setupBlockedMembersObserver() {
        viewModel.blockedMembers.observe(this) { uiState ->
            handleBlockedMembersErrors(uiState)
            if (!uiState.isLoading) blockedMemberAdapter.submitList(uiState.blockedMembers)
        }
    }

    private fun handleBlockedMembersErrors(uiState: BlockedMembersUiState) {
        when {
            uiState.isFetchingError -> showBlockedMemberFetchingErrorMessage()
            uiState.isDeletingBlockedMemberError -> showBlockedMemberDeletingErrorMessage()
        }
    }

    private fun showBlockedMemberFetchingErrorMessage() {
        binding.root.showSnackbar(R.string.memberblock_loading_blocked_members_failed_message)
    }

    private fun showBlockedMemberDeletingErrorMessage() {
        binding.root.showSnackbar(R.string.memberblock_unblock_member_failed_message)
    }

    private fun showUnblockMemberDialog(blockId: Long) {
        ConfirmDialog(
            context = this,
            title = getString(R.string.memberblock_unblock_member_dialog_title),
            message = getString(R.string.memberblock_unblock_member_dialog_message),
            onPositiveButtonClick = { viewModel.unblockMember(blockId) }
        ).show()
    }
}