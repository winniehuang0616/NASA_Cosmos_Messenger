package com.example.nasacosmosmessengerapp.presentation.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.nasacosmosmessengerapp.presentation.theme.NASACosmosMessengerAPPTheme

private data class FavoritePlaceholderItem(
    val id: String,
    val imageUrl: String,
    val dateLabel: String,
    val description: String
)

@Composable
fun FavoritesScreen(modifier: Modifier = Modifier) {
    val items = remember {
        listOf(
            FavoritePlaceholderItem(
                id = "1",
                imageUrl = "https://images-assets.nasa.gov/image/PIA12348/PIA12348~thumb.jpg",
                dateLabel = "(今日)",
                description = "獵戶座大星雲 — Astronomy Picture of the Day 精選"
            ),
            FavoritePlaceholderItem(
                id = "2",
                imageUrl = "https://images-assets.nasa.gov/image/PIA04921/PIA04921~thumb.jpg",
                dateLabel = "(1990/08/08)",
                description = "Astronomy Picture of the Day 經典影像"
            ),
            FavoritePlaceholderItem(
                id = "3",
                imageUrl = "https://images-assets.nasa.gov/image/PIA15413/PIA15413~thumb.jpg",
                dateLabel = "(1999/12/31)",
                description = "星系與深空天體 — 收藏預覽文案"
            ),
            FavoritePlaceholderItem(
                id = "4",
                imageUrl = "https://images-assets.nasa.gov/image/PIA22091/PIA22091~thumb.jpg",
                dateLabel = "(2001/05/05)",
                description = "行星與衛星 — 佔位描述文字"
            ),
            FavoritePlaceholderItem(
                id = "5",
                imageUrl = "https://images-assets.nasa.gov/image/PIA18182/PIA18182~thumb.jpg",
                dateLabel = "(2015/07/14)",
                description = "冥王星近照 — NASA New Horizons"
            ),
            FavoritePlaceholderItem(
                id = "6",
                imageUrl = "https://images-assets.nasa.gov/image/PIA16831/PIA16831~thumb.jpg",
                dateLabel = "(1988/03/20)",
                description = "星雲與塵埃雲 — 收藏列表預留"
            )
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "收藏",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items, key = { it.id }) { item ->
                FavoritePlaceholderCard(item = item)
            }
        }
    }
}

@Composable
private fun FavoritePlaceholderCard(
    item: FavoritePlaceholderItem,
    modifier: Modifier = Modifier
) {
    var isStarred by remember(item.id) { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .clickable { isStarred = !isStarred },
                    shape = CircleShape,
                    color = if (isStarred) {
                        Color.White
                    } else {
                        Color.Black.copy(alpha = 0.38f)
                    },
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "切換收藏",
                            tint = if (isStarred) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.White
                            },
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = item.dateLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenPreview() {
    NASACosmosMessengerAPPTheme {
        FavoritesScreen()
    }
}
