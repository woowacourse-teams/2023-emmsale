package com.emmsale.presentation.ui.eventSearch

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.emmsale.databinding.ActivityEventSearchBinding

class EventSearchActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEventSearchBinding.inflate(layoutInflater) }
    private val viewModel: EventSearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        setupSearchResultRecyclerView()
    }

    private fun setupBinding() {
        setContentView(binding.root)
        binding.viewModel = viewModel
    }

    private fun setupSearchResultRecyclerView() {
        binding.rvEventSearchResult.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL),
        )
    }
}
