package com.example.nasacosmosmessengerapp.presentation.favorites

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.LinearGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.nasacosmosmessengerapp.presentation.theme.NASACosmosMessengerAPPTheme
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    FavoritesScreenContent(
        state = state,
        modifier = modifier,
        onDeleteFavorite = viewModel::onDeleteFavorite
    )
}

@Composable
fun FavoritesScreenContent(
    state: FavoritesUiState,
    modifier: Modifier = Modifier,
    onDeleteFavorite: (String) -> Unit
) {
    var selectedItem by remember { mutableStateOf<FavoriteItemUi?>(null) }

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
            items(
                items = state.items,
                key = { it.date }
            ) { item ->
                FavoriteCard(
                    item = item,
                    onDeleteFavorite = { onDeleteFavorite(item.date) },
                    onOpenDetail = { selectedItem = item }
                )
            }
        }

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "尚無收藏，請在 Nova 長按 APOD 卡片加入收藏",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    selectedItem?.let { item ->
        BirthdaySkyCardDialog(
            item = item,
            onDismiss = { selectedItem = null }
        )
    }
}

@Composable
private fun FavoriteCard(
    item: FavoriteItemUi,
    onDeleteFavorite: () -> Unit,
    onOpenDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenDetail() },
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
                        .clickable { onDeleteFavorite() },
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.45f),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "刪除收藏",
                            tint = Color.White,
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
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
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

@Composable
private fun BirthdaySkyCardDialog(
    item: FavoriteItemUi,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isSharing by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "生日星空卡",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = {
                        if (isSharing) return@IconButton
                        isSharing = true
                        scope.launch {
                            val imageUri = withContext(Dispatchers.IO) {
                                buildShareableImageUri(context, item)
                            }
                            isSharing = false
                            if (imageUri == null) {
                                Toast.makeText(context, "圖片分享失敗，請稍後再試", Toast.LENGTH_SHORT).show()
                                return@launch
                            }
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "image/*"
                                putExtra(Intent.EXTRA_STREAM, imageUri)
                                putExtra(Intent.EXTRA_TEXT, "宇宙祝你生日快樂")
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(
                                Intent.createChooser(shareIntent, "分享生日星空卡")
                            )
                        }
                    },
                    enabled = !isSharing
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "分享"
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    text = item.dateLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        },
        confirmButton = {
            FilledTonalButton(onClick = onDismiss) {
                Text("關閉")
            }
        }
    )
}

private suspend fun buildShareableImageUri(
    context: android.content.Context,
    item: FavoriteItemUi
): android.net.Uri? {
    val request = ImageRequest.Builder(context)
        .data(item.imageUrl)
        .allowHardware(false)
        .build()
    val result = context.imageLoader.execute(request)
    val drawable = (result as? SuccessResult)?.drawable as? BitmapDrawable ?: return null
    val bitmap: Bitmap = drawable.bitmap
    val cardBitmap = buildBirthdayCardBitmap(item = item, imageBitmap = bitmap) ?: return null

    val directory = File(context.cacheDir, "shared_images").apply { mkdirs() }
    val file = File(directory, "birthday_sky_card_${item.date}.jpg")
    FileOutputStream(file).use { out ->
        cardBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
    }

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

private fun buildBirthdayCardBitmap(
    item: FavoriteItemUi,
    imageBitmap: Bitmap
): Bitmap? {
    if (imageBitmap.width <= 0 || imageBitmap.height <= 0) return null

    val width = 1080
    val height = 1350
    val outerPadding = 48f
    val cardLeft = outerPadding
    val cardTop = 48f
    val cardRight = width - outerPadding
    val cardBottom = height - 48f
    val cardRect = RectF(cardLeft, cardTop, cardRight, cardBottom)
    val corner = 38f

    val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(result)
    val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = LinearGradient(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            AndroidColor.parseColor("#EEEFFC"),
            AndroidColor.parseColor("#F6F3FF"),
            Shader.TileMode.CLAMP
        )
    }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

    val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.argb(40, 0, 0, 0)
    }
    canvas.drawRoundRect(
        RectF(cardRect.left + 6f, cardRect.top + 8f, cardRect.right + 6f, cardRect.bottom + 8f),
        corner,
        corner,
        shadowPaint
    )

    val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.WHITE
    }
    canvas.drawRoundRect(cardRect, corner, corner, cardPaint)

    val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.parseColor("#1B1A25")
        textSize = 64f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    canvas.drawText("生日星空卡", cardRect.left + 44f, cardRect.top + 92f, titlePaint)

    val imageRect = RectF(
        cardRect.left + 36f,
        cardRect.top + 130f,
        cardRect.right - 36f,
        cardRect.top + 760f
    )
    val saveCount = canvas.save()
    val clipPath = android.graphics.Path().apply {
        addRoundRect(imageRect, 26f, 26f, android.graphics.Path.Direction.CW)
    }
    canvas.clipPath(clipPath)
    val srcRect = centerCropSrcRect(
        srcWidth = imageBitmap.width,
        srcHeight = imageBitmap.height,
        dstWidth = (imageRect.right - imageRect.left).roundToInt(),
        dstHeight = (imageRect.bottom - imageRect.top).roundToInt()
    )
    val dstRect = Rect(
        imageRect.left.roundToInt(),
        imageRect.top.roundToInt(),
        imageRect.right.roundToInt(),
        imageRect.bottom.roundToInt()
    )
    canvas.drawBitmap(imageBitmap, srcRect, dstRect, null)
    canvas.restoreToCount(saveCount)

    val namePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.parseColor("#1E1D2A")
        textSize = 54f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val datePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.parseColor("#5A5A5A")
        textSize = 46f
    }

    drawSingleLineEllipsizedText(
        canvas = canvas,
        text = item.title,
        x = cardRect.left + 40f,
        y = cardRect.top + 860f,
        maxWidth = cardRect.width() - 80f,
        paint = namePaint
    )
    canvas.drawText(item.dateLabel, cardRect.left + 40f, cardRect.top + 936f, datePaint)

    return result
}

private fun centerCropSrcRect(
    srcWidth: Int,
    srcHeight: Int,
    dstWidth: Int,
    dstHeight: Int
): Rect {
    if (srcWidth <= 0 || srcHeight <= 0 || dstWidth <= 0 || dstHeight <= 0) {
        return Rect(0, 0, srcWidth.coerceAtLeast(1), srcHeight.coerceAtLeast(1))
    }

    val srcRatio = srcWidth.toFloat() / srcHeight.toFloat()
    val dstRatio = dstWidth.toFloat() / dstHeight.toFloat()

    return if (srcRatio > dstRatio) {
        val croppedWidth = (srcHeight * dstRatio).roundToInt().coerceAtMost(srcWidth)
        val left = ((srcWidth - croppedWidth) / 2f).roundToInt().coerceAtLeast(0)
        Rect(left, 0, left + croppedWidth, srcHeight)
    } else {
        val croppedHeight = (srcWidth / dstRatio).roundToInt().coerceAtMost(srcHeight)
        val top = ((srcHeight - croppedHeight) / 2f).roundToInt().coerceAtLeast(0)
        Rect(0, top, srcWidth, top + croppedHeight)
    }
}

private fun drawSingleLineEllipsizedText(
    canvas: Canvas,
    text: String,
    x: Float,
    y: Float,
    maxWidth: Float,
    paint: Paint
) {
    val safeText = if (paint.measureText(text) <= maxWidth) {
        text
    } else {
        var end = text.length
        while (end > 0 && paint.measureText(text.substring(0, end) + "...") > maxWidth) {
            end--
        }
        text.substring(0, end).trimEnd() + "..."
    }
    canvas.drawText(safeText, x, y, paint)
}

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenPreview() {
    NASACosmosMessengerAPPTheme {
        FavoritesScreenContent(
            state = FavoritesUiState.initial(),
            onDeleteFavorite = {}
        )
    }
}
