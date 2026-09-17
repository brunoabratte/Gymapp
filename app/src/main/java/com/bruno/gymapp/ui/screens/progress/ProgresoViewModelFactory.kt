package com.bruno.gymapp.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bruno.gymapp.data.repository.GymRepository

class ProgresoViewModelFactory(
    private val repo: GymRepository,
    private val ejercicioId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ProgresoViewModel(repo, ejercicioId) as T
    }
}
