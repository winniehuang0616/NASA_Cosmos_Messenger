package com.example.nasacosmosmessengerapp.presentation.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasacosmosmessengerapp.data.local.RoomModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val favoriteDao = RoomModule.provideDatabase(application).favoriteApodDao()

    private val _uiState = MutableStateFlow(FavoritesUiState.initial())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            favoriteDao.observeFavoriteDetails().collect { rows ->
                _uiState.update {
                    it.copy(
                        items = rows.map { row ->
                            FavoriteItemUi(
                                date = row.date,
                                imageUrl = if (row.mediaType == "image") {
                                    row.hdUrl ?: row.url
                                } else {
                                    row.thumbnailUrl ?: row.url
                                },
                                dateLabel = row.date,
                                title = row.title,
                                description = row.explanation
                            )
                        },
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onDeleteFavorite(date: String) {
        viewModelScope.launch {
            favoriteDao.deleteFavorite(date)
        }
    }
}
