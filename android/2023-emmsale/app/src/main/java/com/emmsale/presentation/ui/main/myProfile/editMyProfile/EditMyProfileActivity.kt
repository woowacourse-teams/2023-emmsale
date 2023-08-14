package com.emmsale.presentation.ui.main.myProfile.editMyProfile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.databinding.ActivityEditMyProfileBinding

class EditMyProfileActivity : AppCompatActivity() {

    private val binding: ActivityEditMyProfileBinding by lazy {
        ActivityEditMyProfileBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolbar()
    }

    private fun initToolbar() {
        binding.tbEditmyprofileToolbar.setNavigationOnClickListener { finish() }
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, EditMyProfileActivity::class.java))
        }
    }
}
