package com.emmsale.presentation.ui.setting.block

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityMemberBlockBinding
import com.emmsale.presentation.common.extension.showSnackbar
import com.emmsale.presentation.ui.setting.block.recyclerview.BlockedMemberAdapter

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
            when {
                uiState.isFetchingError -> showBlockedMemberFetchingErrorMessage()
                !uiState.isLoading -> blockedMemberAdapter.submitList(uiState.blockedMembers)
            }
        }
    }

    private fun showBlockedMemberFetchingErrorMessage() {
        binding.root.showSnackbar(R.string.memberblock_loading_blocked_members_failed_message)
    }

    private fun showUnblockMemberDialog(memberId: Long) {

    }
}