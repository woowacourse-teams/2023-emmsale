package com.emmsale.presentation.ui.messageRoomList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentMessageRoomBinding
import com.emmsale.presentation.base.BaseFragment
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.ui.messageList.MessageListActivity
import com.emmsale.presentation.ui.messageRoomList.recyclerview.MessageRoomListAdapter

class MessageRoomFragment : BaseFragment<FragmentMessageRoomBinding>() {
    override val layoutResId: Int = R.layout.fragment_message_room
    private val viewModel: MessageRoomViewModel by viewModels { MessageRoomViewModel.factory }

    private lateinit var messageRoomListAdapter: MessageRoomListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBinding()
        setupMessageRoomRecyclerView()
        setupMessageRooms()
    }

    private fun setupBinding() {
        binding.lifecycleOwner = this
        binding.vm = viewModel
    }

    private fun setupMessageRoomRecyclerView() {
        messageRoomListAdapter = MessageRoomListAdapter(::navigateToMessageList)
        binding.rvMessageRoomList.adapter = messageRoomListAdapter
    }

    private fun setupMessageRooms() {
        viewModel.messageRooms.observe(viewLifecycleOwner) { uiState ->
            if (uiState.fetchResult != FetchResult.SUCCESS) return@observe
            messageRoomListAdapter.submitList(uiState.messageRooms)
        }
    }

    private fun navigateToMessageList(roomId: String, otherUid: Long) {
        MessageListActivity.startActivity(requireContext(), roomId, otherUid)
    }

    companion object {
        const val TAG: String = "TAG_MESSAGE_ROOM"
    }
}
