package com.bruno.gymapp.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bruno.gymapp.data.repository.GymRepository

class HistorialViewModelFactory(private val repo: GymRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HistorialViewModel(repo) as T
    }
}
