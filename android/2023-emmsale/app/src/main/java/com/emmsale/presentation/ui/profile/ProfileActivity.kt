package com.emmsale.presentation.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
    }

    companion object {
        private const val KEY_MEMBER_ID = "KEY_MEMBER_ID"

        fun startActivity(context: Context, memberId: Long) {
            val intent = Intent(context, ProfileActivity::class.java).apply {
                putExtra(KEY_MEMBER_ID, memberId)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

            context.startActivity(intent)
        }
    }
}
