package com.example.nasacosmosmessengerapp.presentation.favorites

data class FavoriteItemUi(
    val id: String,
    val imageUrl: String,
    val dateLabel: String,
    val description: String
)

data class FavoritesUiState(
    val items: List<FavoriteItemUi>,
    val starredIds: Set<String>
) {
    companion object {
        fun initial(): FavoritesUiState = FavoritesUiState(
            items = listOf(
                FavoriteItemUi(
                    id = "1",
                    imageUrl = "https://images-assets.nasa.gov/image/PIA12348/PIA12348~thumb.jpg",
                    dateLabel = "(今日)",
                    description = "獵戶座大星雲 — Astronomy Picture of the Day 精選"
                ),
                FavoriteItemUi(
                    id = "2",
                    imageUrl = "https://images-assets.nasa.gov/image/PIA04921/PIA04921~thumb.jpg",
                    dateLabel = "(1990/08/08)",
                    description = "Astronomy Picture of the Day 經典影像"
                ),
                FavoriteItemUi(
                    id = "3",
                    imageUrl = "https://images-assets.nasa.gov/image/PIA15413/PIA15413~thumb.jpg",
                    dateLabel = "(1999/12/31)",
                    description = "星系與深空天體 — 收藏預覽文案"
                ),
                FavoriteItemUi(
                    id = "4",
                    imageUrl = "https://images-assets.nasa.gov/image/PIA22091/PIA22091~thumb.jpg",
                    dateLabel = "(2001/05/05)",
                    description = "行星與衛星 — 佔位描述文字"
                ),
                FavoriteItemUi(
                    id = "5",
                    imageUrl = "https://images-assets.nasa.gov/image/PIA18182/PIA18182~thumb.jpg",
                    dateLabel = "(2015/07/14)",
                    description = "冥王星近照 — NASA New Horizons"
                ),
                FavoriteItemUi(
                    id = "6",
                    imageUrl = "https://images-assets.nasa.gov/image/PIA16831/PIA16831~thumb.jpg",
                    dateLabel = "(1988/03/20)",
                    description = "星雲與塵埃雲 — 收藏列表預留"
                )
            ),
            starredIds = emptySet()
        )
    }
}
