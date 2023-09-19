package com.emmsale.presentation.ui.messageRoomList

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.databinding.ActivityMessageRoomListBinding
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.ui.messageList.MessageListActivity
import com.emmsale.presentation.ui.messageRoomList.recyclerview.MessageRoomListAdapter

class MessageRoomListActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMessageRoomListBinding.inflate(layoutInflater) }
    private val viewModel: MessageRoomListViewModel by viewModels { MessageRoomListViewModel.factory }

    private lateinit var messageRoomListAdapter: MessageRoomListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        setupMessageRoomRecyclerView()
        setupMessageRooms()
    }

    private fun setupBinding() {
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.vm = viewModel
    }

    private fun setupMessageRoomRecyclerView() {
        messageRoomListAdapter = MessageRoomListAdapter(::navigateToMessageList)
        binding.rvMessageRoomList.adapter = messageRoomListAdapter
    }

    private fun setupMessageRooms() {
        viewModel.messageRooms.observe(this) { uiState ->
            if (uiState.fetchResult != FetchResult.SUCCESS) return@observe
            messageRoomListAdapter.submitList(uiState.messageRooms)
        }
    }

    private fun navigateToMessageList(roomId: String, otherUid: Long) {
        MessageListActivity.startActivity(this, roomId, otherUid)
    }
}
