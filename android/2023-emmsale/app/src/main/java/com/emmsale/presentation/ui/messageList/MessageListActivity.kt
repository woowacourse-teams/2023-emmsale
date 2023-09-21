package com.emmsale.presentation.ui.messageList

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.emmsale.R
import com.emmsale.databinding.ActivityMessageListBinding
import com.emmsale.presentation.common.EventObserver
import com.emmsale.presentation.common.FetchResult
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.ui.messageList.recyclerview.MessageListAdapter
import com.emmsale.presentation.ui.messageList.uistate.MessageListUiEvent
import com.emmsale.presentation.ui.profile.ProfileActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MessageListActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMessageListBinding.inflate(layoutInflater) }
    private val viewModel: MessageListViewModel by viewModels {
        MessageListViewModel.factory(
            roomId = intent.getStringExtra(KEY_ROOM_ID) ?: DEFAULT_ROOM_ID,
            otherUid = intent.getLongExtra(KEY_OTHER_UID, DEFAULT_OTHER_ID),
        )
    }

    private var job: Job? = null

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
    }

    private fun setupMessageRecyclerView() {
        messageListAdapter = MessageListAdapter(::navigateToProfile)
        binding.rvMessageList.setHasFixedSize(true)
        binding.rvMessageList.itemAnimator = null
        binding.rvMessageList.adapter = messageListAdapter
    }

    private fun navigateToProfile(uid: Long) {
        ProfileActivity.startActivity(this, uid)
    }

    private fun setupMessages() {
        viewModel.messages.observe(this) { uiState ->
            if (uiState.fetchResult != FetchResult.SUCCESS) return@observe
            messageListAdapter.submitList(uiState.messages)
        }
    }

    private fun scrollToEnd() {
        binding.rvMessageList.smoothScrollToPosition(viewModel.messages.value.messageSize)
    }

    private fun setUpEventUiEvent() {
        viewModel.uiEvent.observe(this, EventObserver(::handleEvent))
    }

    private fun handleEvent(event: MessageListUiEvent) {
        when (event) {
            MessageListUiEvent.MESSAGE_LIST_FIRST_LOADED -> scrollToEnd()
            MessageListUiEvent.MESSAGE_SENDING -> binding.etMessageInput.text.clear()
            MessageListUiEvent.MESSAGE_SENT_REFRESHED -> scrollToEnd()
            MessageListUiEvent.MESSAGE_SENT_FAILED -> binding.root.showSnackBar(R.string.messagelist_message_sent_failed)
            MessageListUiEvent.NOT_FOUND_OTHER_MEMBER -> binding.root.showSnackBar(R.string.messagelist_not_found_other_member)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        viewModel.refresh()

        val profileUrl = intent?.getStringExtra(KEY_PROFILE_URL)
        val otherName = intent?.getStringExtra(KEY_OTHER_NAME) ?: return
        val messageContent = intent.getStringExtra(KEY_MESSAGE_CONTENT) ?: return
        showNewMessage(profileUrl, otherName, messageContent)
    }

    private fun showNewMessage(profileUrl: String?, otherName: String, messageContent: String) {
        val layoutManager = binding.rvMessageList.layoutManager as LinearLayoutManager
        val lastVisiblePos = layoutManager.findLastVisibleItemPosition()
        val itemCount = viewModel.messages.value.messages.size
        if (lastVisiblePos <= itemCount - 3) {
            job?.cancel()
            job = lifecycleScope.launch {
                showBottomMessage(profileUrl, otherName, messageContent)
                delay(4000)
                hideBottomMessage()
            }
        }
    }

    private fun showBottomMessage(profileUrl: String?, otherName: String, messageContent: String) {
        binding.clNewMessage.setOnClickListener { scrollToEnd() }
        binding.clNewMessage.visibility = View.VISIBLE
        Glide.with(this@MessageListActivity)
            .load(profileUrl)
            .fallback(R.drawable.ic_message_default_profile)
            .into(binding.ivMemberProfile)
        binding.tvNewMessage.text = messageContent
        binding.tvSenderName.text = otherName
    }

    private fun hideBottomMessage() {
        binding.clNewMessage.visibility = View.GONE
    }

    companion object {
        private const val KEY_ROOM_ID = "KEY_ROOM_ID"
        private const val DEFAULT_ROOM_ID = ""

        private const val KEY_OTHER_UID = "KEY_OTHER_UID"
        private const val DEFAULT_OTHER_ID = -1L

        private const val KEY_PROFILE_URL = "KEY_PROFILE_URL"
        private const val KEY_OTHER_NAME = "KEY_OTHER_NAME"
        private const val KEY_MESSAGE_CONTENT = "KEY_MESSAGE_CONTENT"

        fun getIntent(
            context: Context,
            roomId: String,
            otherUid: Long,
        ) = Intent(context, MessageListActivity::class.java)
            .putExtra(KEY_ROOM_ID, roomId)
            .putExtra(KEY_OTHER_UID, otherUid)

        fun getIntent(
            context: Context,
            roomId: String,
            otherUid: Long,
            profileUrl: String?,
            otherName: String,
            messageContent: String,
        ) = getIntent(context, roomId, otherUid)
            .putExtra(KEY_PROFILE_URL, profileUrl)
            .putExtra(KEY_OTHER_NAME, otherName)
            .putExtra(KEY_MESSAGE_CONTENT, messageContent)
    }
}