package com.bruno.gymapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bruno.gymapp.data.ConfiguracionEntrenamiento
import com.bruno.gymapp.data.repository.GymRepository

class HomeViewModelFactory(private val repo: GymRepository, private val config: ConfiguracionEntrenamiento) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST") return HomeViewModel(repo, config) as T
    }
}
