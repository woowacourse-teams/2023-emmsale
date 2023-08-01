package com.emmsale.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory<T : ViewModel>(
    private val create: () -> T,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = create()
        if (modelClass.isAssignableFrom(viewModel::class.java)) {
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
