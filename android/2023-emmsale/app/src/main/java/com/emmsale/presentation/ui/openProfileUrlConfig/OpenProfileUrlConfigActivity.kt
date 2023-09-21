package com.emmsale.presentation.ui.openProfileUrlConfig

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R
import com.emmsale.databinding.ActivityOpenProfileUrlConfigBinding
import com.emmsale.presentation.common.extension.showSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OpenProfileUrlConfigActivity : AppCompatActivity() {

    private val binding: ActivityOpenProfileUrlConfigBinding by lazy {
        ActivityOpenProfileUrlConfigBinding.inflate(layoutInflater)
    }
    private val viewModel: OpenProfileUrlConfigViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initToolbar()
        setUpIsUpdateUrlSuccess()
        setUpOpenProfileUrlConfig()
        initCompleteButtonClick()
    }

    private fun initBinding() {
        setContentView(binding.root)
        binding.vm = viewModel
        binding.lifecycleOwner = this
    }

    private fun initToolbar() {
        binding.tbOpenProfileUrlConfig.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.close -> finish()
            }
            true
        }
    }

    private fun setUpOpenProfileUrlConfig() {
        viewModel.isUrlFetchError.observe(this) { isError ->
            if (isError) {
                binding.root.showSnackBar(getString(R.string.all_data_loading_failed_message))
                finish()
            }
        }
    }

    private fun setUpIsUpdateUrlSuccess() {
        viewModel.isUpdateUrlSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                binding.root.showSnackBar(getString(R.string.openprofileurlconfig_register_success))
                finish()
            } else {
                binding.root.showSnackBar(getString(R.string.openprofileurlconfig_register_fail))
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
