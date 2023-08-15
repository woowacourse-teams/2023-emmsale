package com.emmsale.presentation.ui.setting.block

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.databinding.ActivityMemberBlockBinding

class MemberBlockActivity : AppCompatActivity() {
    private val binding: ActivityMemberBlockBinding by lazy {
        ActivityMemberBlockBinding.inflate(layoutInflater)
    }
    private val viewModel: MemberBlockViewModel by viewModels { MemberBlockViewModel.factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}