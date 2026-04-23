package com.example.nasacosmosmessengerapp.presentation.favorites

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FavoritesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState.initial())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    fun onToggleStar(id: String) {
        _uiState.update { state ->
            val next = state.starredIds.toMutableSet()
            if (id in next) next.remove(id) else next.add(id)
            state.copy(starredIds = next)
        }
    }
}
