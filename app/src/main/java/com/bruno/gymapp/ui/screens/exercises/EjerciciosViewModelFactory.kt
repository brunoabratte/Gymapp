package com.bruno.gymapp.ui.screens.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bruno.gymapp.data.repository.GymRepository

class EjerciciosViewModelFactory(private val repo: GymRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EjerciciosViewModel(repo) as T
    }
}
