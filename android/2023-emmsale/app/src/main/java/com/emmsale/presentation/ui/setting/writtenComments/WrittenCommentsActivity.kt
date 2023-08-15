package com.emmsale.presentation.ui.setting.writtenComments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.databinding.ActivityWrittenCommentsBinding

class WrittenCommentsActivity : AppCompatActivity() {

    private val binding: ActivityWrittenCommentsBinding by lazy {
        ActivityWrittenCommentsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolbar()
    }

    private fun initToolbar() {
        binding.tvWrittencommentsToolbar.setNavigationOnClickListener { finish() }
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, WrittenCommentsActivity::class.java))
        }
    }
}
