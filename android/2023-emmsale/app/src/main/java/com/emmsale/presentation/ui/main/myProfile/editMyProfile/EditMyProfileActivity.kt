package com.emmsale.presentation.ui.main.myProfile.editMyProfile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R

class EditMyProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_my_profile)
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, EditMyProfileActivity::class.java))
        }
    }
}
