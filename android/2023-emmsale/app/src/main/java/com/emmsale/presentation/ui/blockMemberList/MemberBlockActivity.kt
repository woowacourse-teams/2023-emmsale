package com.emmsale.presentation.ui.blockMemberList

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityMemberBlockBinding
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.common.views.ConfirmDialog
import com.emmsale.presentation.ui.blockMemberList.recyclerView.BlockedMemberAdapter
import com.emmsale.presentation.ui.blockMemberList.uiState.BlockedMembersUiEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MemberBlockActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMemberBlockBinding.inflate(layoutInflater) }
    private val viewModel: MemberBlockViewModel by viewModels()
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
        viewModel.blockedMembers.observe(this) { uiState ->
            if (!uiState.isLoading) blockedMemberAdapter.submitList(uiState.blockedMembers)
        }
    }

    private fun setupUiEvent() {
        viewModel.event.observe(this) {
            handleEvent(it)
        }
    }

    private fun handleEvent(event: BlockedMembersUiEvent?) {
        if (event == null) return
        when (event) {
            BlockedMembersUiEvent.DELETE_ERROR -> showBlockedMemberDeletingErrorMessage()
        }
        viewModel.resetEvent()
    }

    private fun showBlockedMemberDeletingErrorMessage() {
        binding.root.showSnackBar(R.string.memberblock_unblock_member_failed_message)
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
