package com.emmsale.presentation.ui.messageList

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityMessageListBinding
import com.emmsale.presentation.common.EventObserver
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.ui.messageList.recyclerview.MessageListAdapter
import com.emmsale.presentation.ui.messageList.uistate.MessageListUiEvent
import com.emmsale.presentation.ui.messageList.uistate.MessagesUiState

class MessageListActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMessageListBinding.inflate(layoutInflater) }
    private val viewModel: MessageListViewModel by viewModels {
        MessageListViewModel.factory(
            roomId = intent.getStringExtra(KEY_ROOM_ID) ?: DEFAULT_ROOM_ID,
            otherUid = intent.getLongExtra(KEY_OTHER_UID, DEFAULT_OTHER_ID),
        )
    }

    private lateinit var messageListAdapter: MessageListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        setupToolbar()
        setupMessageRecyclerView()
        setupMessages()
        setUpEventUiEvent()
    }

    private fun setupBinding() {
        setContentView(binding.root)
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    private fun setupToolbar() {
        binding.tbMessageList.setNavigationOnClickListener {
            finish()
        }
        binding.tbMessageList.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.refresh) viewModel.refresh()
            true
        }
    }

    private fun setupMessageRecyclerView() {
        messageListAdapter = MessageListAdapter()
        binding.rvMessageList.setHasFixedSize(true)
        binding.rvMessageList.itemAnimator = null
        binding.rvMessageList.adapter = messageListAdapter
    }

    private fun setupMessages() {
        viewModel.messages.observe(this) { uiState ->
            if (uiState.fetchResult != FetchResult.SUCCESS) return@observe
            messageListAdapter.submitList(uiState.messages) {
                scrollToEnd(uiState)
            }
        }
    }

    private fun scrollToEnd(uiState: MessagesUiState) {
        binding.rvMessageList.smoothScrollToPosition(uiState.messages.size)
    }

    private fun setUpEventUiEvent() {
        viewModel.uiEvent.observe(this, EventObserver(::handleEvent))
    }

    private fun handleEvent(event: MessageListUiEvent) {
        when (event) {
            MessageListUiEvent.MESSAGE_SENT -> binding.etMessageInput.text.clear()
            MessageListUiEvent.NOT_FOUND_OTHER_MEMBER -> binding.root.showSnackBar(R.string.messagelist_not_found_other_member)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        viewModel.refresh()
    }

    companion object {
        private const val KEY_ROOM_ID = "KEY_ROOM_ID"
        private const val DEFAULT_ROOM_ID = ""

        private const val KEY_OTHER_UID = "KEY_OTHER_UID"
        private const val DEFAULT_OTHER_ID = -1L

        fun startActivity(context: Context, roomId: String, otherUid: Long) {
            context.startActivity(getIntent(context, roomId, otherUid))
        }

        fun getIntent(
            context: Context,
            roomId: String,
            otherUid: Long,
        ) = Intent(context, MessageListActivity::class.java)
            .putExtra(KEY_ROOM_ID, roomId)
            .putExtra(KEY_OTHER_UID, otherUid)
    }
}
