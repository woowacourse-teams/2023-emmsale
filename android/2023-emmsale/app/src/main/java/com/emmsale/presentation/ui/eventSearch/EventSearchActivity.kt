package com.emmsale.presentation.ui.eventSearch

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.emmsale.databinding.ActivityEventSearchBinding
import com.emmsale.presentation.ui.eventSearch.recyclerView.EventSearchAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventSearchActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEventSearchBinding.inflate(layoutInflater) }
    private val viewModel: EventSearchViewModel by viewModels()
    private val eventSearchAdapter: EventSearchAdapter by lazy { EventSearchAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        setupSearchResultRecyclerView()
        collectEventSearchResults()
    }

    private fun setupBinding() {
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun setupSearchResultRecyclerView() {
        with(binding.rvEventSearchResult) {
            adapter = eventSearchAdapter
            addItemDecoration(
                DividerItemDecoration(this@EventSearchActivity, DividerItemDecoration.VERTICAL),
            )
        }
    }

    private fun collectEventSearchResults() {
        viewModel.eventSearchResults.observe(this) { eventSearchUiState ->
            eventSearchAdapter.submitList(eventSearchUiState.events)
        }
    }
}
