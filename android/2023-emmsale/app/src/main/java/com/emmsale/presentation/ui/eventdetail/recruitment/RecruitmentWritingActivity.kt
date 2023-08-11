package com.emmsale.presentation.ui.eventdetail.recruitment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.R

class RecruitmentWritingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recruitment_writing)
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, RecruitmentWritingActivity::class.java)
            context.startActivity(intent)
        }
    }
}
