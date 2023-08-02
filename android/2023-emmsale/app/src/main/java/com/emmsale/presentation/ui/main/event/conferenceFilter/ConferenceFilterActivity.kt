package com.emmsale.presentation.ui.main.event.conferenceFilter

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.emmsale.databinding.ActivityConferenceFilterBinding

class ConferenceFilterActivity : AppCompatActivity() {
    private val viewModel: ConferenceFilterViewModel by viewModels { ConferenceFilterViewModel.factory }
    private val binding: ActivityConferenceFilterBinding by lazy {
        ActivityConferenceFilterBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


    }
}
