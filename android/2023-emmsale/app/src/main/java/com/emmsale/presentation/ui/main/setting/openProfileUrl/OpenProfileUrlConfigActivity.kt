package com.emmsale.presentation.ui.main.setting.openProfileUrl

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityOpenProfileUrlConfigBinding
import com.emmsale.presentation.common.extension.showToast

class OpenProfileUrlConfigActivity : AppCompatActivity() {

    private val binding: ActivityOpenProfileUrlConfigBinding by lazy {
        ActivityOpenProfileUrlConfigBinding.inflate(layoutInflater)
    }
    private val viewModel: OpenProfileUrlConfigViewModel by viewModels {
        OpenProfileUrlConfigViewModel.factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setUpIsUpdateUrlSuccess()
        setUpOpenProfileUrlConfig()
        initCompleteButtonClick()
    }

    private fun initBinding() {
        setContentView(binding.root)
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    private fun setUpOpenProfileUrlConfig() {
        viewModel.isUrlFetchError.observe(this) { isError ->
            if (isError) {
                showToast(getString(R.string.all_data_loading_failed_message))
                finish()
            }
        }
    }

    private fun setUpIsUpdateUrlSuccess() {
        viewModel.isUpdateUrlSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                showToast(getString(R.string.openprofileurlconfig_register_success))
                finish()
            } else {
                showToast(getString(R.string.oepnprofileurlconfig_register_fail))
            }
        }
    }

    private fun initCompleteButtonClick() {
        binding.btnOpenProfileUrlConfigComplete.setOnClickListener {
            viewModel.updateOpenProfileUrl(binding.etOpenProfileUrlConfig.text.toString())
        }
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, OpenProfileUrlConfigActivity::class.java)
            context.startActivity(intent)
        }
    }
}