package com.bruno.gymapp.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bruno.gymapp.data.repository.GymRepository

class HistorialDetalleViewModelFactory(
    private val repo: GymRepository,
    private val sesionId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HistorialDetalleViewModel(repo, sesionId) as T
    }
}
