package com.example.nasacosmosmessengerapp.presentation.favorites

data class FavoriteItemUi(
    val date: String,
    val imageUrl: String,
    val dateLabel: String,
    val title: String,
    val description: String
)

data class FavoritesUiState(
    val items: List<FavoriteItemUi>,
    val isLoading: Boolean
) {
    companion object {
        fun initial(): FavoritesUiState = FavoritesUiState(
            items = emptyList(),
            isLoading = true
        )
    }
}
