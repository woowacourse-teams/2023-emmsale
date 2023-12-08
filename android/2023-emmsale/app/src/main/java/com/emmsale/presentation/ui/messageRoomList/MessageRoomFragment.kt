package com.emmsale.presentation.ui.messageRoomList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.emmsale.R
import com.emmsale.databinding.FragmentMessageRoomBinding
import com.emmsale.presentation.base.NetworkFragment
import com.emmsale.presentation.ui.messageList.MessageListActivity
import com.emmsale.presentation.ui.messageRoomList.recyclerview.MessageRoomListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessageRoomFragment : NetworkFragment<FragmentMessageRoomBinding>() {
    override val layoutResId: Int = R.layout.fragment_message_room
    override val viewModel: MessageRoomViewModel by viewModels()

    private val messageRoomListAdapter by lazy { MessageRoomListAdapter(::navigateToMessageList) }

    private fun navigateToMessageList(roomId: String, otherUid: Long) {
        startActivity(MessageListActivity.getIntent(requireContext(), roomId, otherUid))
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDataBinding()
        setupMessageRoomRecyclerView()

        observeMessageRooms()
    }

    private fun setupDataBinding() {
        binding.vm = viewModel
    }

    private fun setupMessageRoomRecyclerView() {
        binding.rvMessageRoomList.adapter = messageRoomListAdapter
    }

    private fun observeMessageRooms() {
        viewModel.messageRooms.observe(viewLifecycleOwner) { messageRooms ->
            messageRoomListAdapter.submitList(messageRooms)
        }
    }

    companion object {
        const val TAG: String = "TAG_MESSAGE_ROOM"
    }
}
