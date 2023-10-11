package com.emmsale.presentation.ui.eventSearch

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.emmsale.databinding.ActivityEventSearchBinding
import com.emmsale.presentation.ui.eventDetail.EventDetailActivity
import com.emmsale.presentation.ui.eventSearch.recyclerView.eventSearch.EventSearchAdapter
import com.emmsale.presentation.ui.eventSearch.recyclerView.eventSearchHistory.EventSearchHistoryAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventSearchActivity : AppCompatActivity() {
    private val binding by lazy { ActivityEventSearchBinding.inflate(layoutInflater) }
    private val viewModel: EventSearchViewModel by viewModels()
    private val eventSearchAdapter by lazy {
        EventSearchAdapter { event ->
            viewModel.saveEventSearch()
            navigateToEventDetail(event.id)
        }
    }
    private val eventSearchHistoryAdapter by lazy {
        EventSearchHistoryAdapter(
            onHistoryClick = { eventSearch -> viewModel.searchEvents(eventSearch.query) },
            onDeleteClick = viewModel::deleteSearchHistory,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        setupSearchResultRecyclerView()
        setupSearchHistoryRecyclerView()
        observeEventSearchResults()
        observeEventSearchHistories()
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

    private fun setupSearchHistoryRecyclerView() {
        binding.rvEventSearchHistory.adapter = eventSearchHistoryAdapter
    }

    private fun observeEventSearchResults() {
        viewModel.eventSearchResults.observe(this) { eventSearchUiState ->
            eventSearchAdapter.submitList(eventSearchUiState.events)
        }
    }

    private fun observeEventSearchHistories() {
        viewModel.eventSearchHistories.observe(this) { eventSearchHistories ->
            eventSearchHistoryAdapter.submitList(eventSearchHistories)
        }
    }

    private fun navigateToEventDetail(eventId: Long) {
        EventDetailActivity.startActivity(this, eventId)
    }
}
