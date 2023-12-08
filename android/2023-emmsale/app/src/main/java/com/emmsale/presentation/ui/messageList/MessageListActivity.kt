package com.emmsale.presentation.ui.messageList

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.emmsale.R
import com.emmsale.databinding.ActivityMessageListBinding
import com.emmsale.presentation.base.NetworkActivity
import com.emmsale.presentation.common.KeyboardHider
import com.emmsale.presentation.common.extension.showSnackBar
import com.emmsale.presentation.ui.messageList.MessageListViewModel.Companion.KEY_OTHER_UID
import com.emmsale.presentation.ui.messageList.MessageListViewModel.Companion.KEY_ROOM_ID
import com.emmsale.presentation.ui.messageList.recyclerview.MessageListAdapter
import com.emmsale.presentation.ui.messageList.uistate.MessageListUiEvent
import com.emmsale.presentation.ui.profile.ProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MessageListActivity :
    NetworkActivity<ActivityMessageListBinding>(R.layout.activity_message_list) {

    override val viewModel: MessageListViewModel by viewModels()
    private val keyboardHider by lazy { KeyboardHider(this) }

    private val messageListAdapter by lazy { MessageListAdapter(onProfileClick = ::navigateToProfile) }

    private var bottomMessageShowingJob: Job? = null

    private fun navigateToProfile(uid: Long) {
        ProfileActivity.startActivity(this, uid)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupDataBinding()
        setupToolbar()
        setupMessageRecyclerView()

        observeMessages()
        observeUiEvent()
    }

    private fun setupDataBinding() {
        binding.vm = viewModel
    }

    private fun setupToolbar() {
        binding.tbMessageList.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupMessageRecyclerView() {
        binding.rvMessageList.itemAnimator = null
        binding.rvMessageList.adapter = messageListAdapter
        binding.rvMessageList.setOnScrollChangeListener { v, _, _, _, _ ->
            if (!v.canScrollVertically(BOTTOM_SCROLL_DIRECTION)) {
                hideBottomMessage()
            }
        }
        binding.rvMessageList.setOnTouchListener { _, event ->
            keyboardHider.handleHideness(event)
        }
    }

    private fun observeMessages() {
        viewModel.messages.observe(this) {
            messageListAdapter.submitList(it.messages)
        }
    }

    private fun observeUiEvent() {
        viewModel.uiEvent.observe(this, ::handleUiEvent)
    }

    private fun handleUiEvent(uiEvent: MessageListUiEvent) {
        when (uiEvent) {
            MessageListUiEvent.MessageSendComplete -> {
                binding.btiwSendMessage.clearText()
                smoothScrollToEnd()
            }

            MessageListUiEvent.MessageSendFail -> binding.root.showSnackBar(R.string.messagelist_message_sent_failed)
        }
    }

    private fun smoothScrollToEnd() {
        binding.rvMessageList.smoothScrollToPosition(viewModel.messages.value.size)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        viewModel.refresh()

        val roomId = intent?.getStringExtra(KEY_ROOM_ID)
        if (roomId != viewModel.roomId) return

        val profileUrl = intent.getStringExtra(KEY_PROFILE_URL)
        val otherName = intent.getStringExtra(KEY_OTHER_NAME) ?: return
        val messageContent = intent.getStringExtra(KEY_MESSAGE_CONTENT) ?: return
        showNewMessage(profileUrl, otherName, messageContent)
    }

    private fun showNewMessage(profileUrl: String?, otherName: String, messageContent: String) {
        val layoutManager = binding.rvMessageList.layoutManager as LinearLayoutManager
        val lastVisiblePos = layoutManager.findLastVisibleItemPosition()
        val itemCount = viewModel.messages.value.size
        val lastPosition = itemCount - 1

        if (lastVisiblePos != lastPosition) {
            bottomMessageShowingJob?.cancel()
            bottomMessageShowingJob = lifecycleScope.launch {
                showBottomMessage(profileUrl, otherName, messageContent)
                delay(4000)
                hideBottomMessage()
            }
        }
    }

    private fun showBottomMessage(profileUrl: String?, otherName: String, messageContent: String) {
        binding.clNewMessage.setOnClickListener {
            it.isVisible = false
            smoothScrollToEnd()
        }
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
        private const val BOTTOM_SCROLL_DIRECTION = 1
        private const val KEY_PROFILE_URL = "KEY_PROFILE_URL"
        private const val KEY_OTHER_NAME = "KEY_OTHER_NAME"
        private const val KEY_MESSAGE_CONTENT = "KEY_MESSAGE_CONTENT"

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
