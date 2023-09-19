package com.emmsale.presentation.ui.messageList

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.databinding.ActivityMessageListBinding

class MessageListActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMessageListBinding.inflate(layoutInflater) }
    private val viewModel: MessageListViewModel by viewModels {
        MessageListViewModel.factory(
            roomId = intent.getStringExtra(KEY_ROOM_ID) ?: DEFAULT_ROOM_ID,
            otherUid = intent.getLongExtra(KEY_OTHER_UID, DEFAULT_OTHER_ID),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
    }

    private fun setupBinding() {
        setContentView(binding.root)
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    companion object {
        private const val KEY_ROOM_ID = "roomId"
        private const val DEFAULT_ROOM_ID = ""

        private const val KEY_OTHER_UID = "otherUid"
        private const val DEFAULT_OTHER_ID = -1L

        fun startActivity(
            context: Context,
            roomId: String,
            otherUid: Long,
        ) {
            val intent = Intent(context, MessageListActivity::class.java)
                .putExtra(KEY_ROOM_ID, roomId)
                .putExtra(KEY_OTHER_UID, otherUid)
            context.startActivity(intent)
        }
    }
}
