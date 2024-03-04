package com.emmsale.presentation.ui.useTerm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.BuildConfig
import com.emmsale.R
import com.emmsale.databinding.ActivityUseTermWebViewBinding
import com.google.firebase.messaging.FirebaseMessaging

class UseTermWebViewActivity : AppCompatActivity() {
    private val binding by lazy { ActivityUseTermWebViewBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initCloseButtonClick()
        initWebViewSetting()
        FirebaseMessaging.getInstance().token
    }

    private fun initWebViewSetting() {
        binding.webviewUseTerm.apply {
            webViewClient = WebViewClient() //
            loadUrl(USE_TERM_URL)
        }
    }

    private fun initCloseButtonClick() {
        binding.tbUseTerm.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.close) finish()
            true
        }
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, UseTermWebViewActivity::class.java)
            context.startActivity(intent)
        }

        private const val USE_TERM_URL = BuildConfig.BASE_URL + "/privacy"
    }
}
