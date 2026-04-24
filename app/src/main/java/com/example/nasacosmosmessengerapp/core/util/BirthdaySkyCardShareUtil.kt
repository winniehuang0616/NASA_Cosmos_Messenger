package com.example.nasacosmosmessengerapp.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.core.content.FileProvider
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

data class BirthdaySkyCardContent(
    val idKey: String,
    val imageUrl: String,
    val dateLabel: String,
    val title: String,
    val description: String
)

suspend fun buildBirthdaySkyCardShareUri(
    context: Context,
    content: BirthdaySkyCardContent
): Uri? {
    val request = ImageRequest.Builder(context)
        .data(content.imageUrl)
        .allowHardware(false)
        .build()
    val result = context.imageLoader.execute(request)
    val drawable = (result as? SuccessResult)?.drawable as? BitmapDrawable ?: return null
    val bitmap: Bitmap = drawable.bitmap
    val cardBitmap = buildBirthdayCardBitmap(content = content, imageBitmap = bitmap) ?: return null

    val directory = File(context.cacheDir, "shared_images").apply { mkdirs() }
    val file = File(directory, "birthday_sky_card_${content.idKey}.jpg")
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
    content: BirthdaySkyCardContent,
    imageBitmap: Bitmap
): Bitmap? {
    if (imageBitmap.width <= 0 || imageBitmap.height <= 0) return null

    val width = 1080
    val height = 1500
    val outerPadding = 48f
    val cardRect = RectF(
        outerPadding,
        48f,
        width - outerPadding,
        height - 48f
    )
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

    val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.parseColor("#1B1A25")
        textSize = 64f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    canvas.drawText("生日星空卡", cardRect.left + 44f, cardRect.top + 92f, headerPaint)

    val imageRect = RectF(
        cardRect.left + 36f,
        cardRect.top + 130f,
        cardRect.right - 36f,
        cardRect.top + 730f
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

    val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.parseColor("#1E1D2A")
        textSize = 50f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val datePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.parseColor("#5A5A5A")
        textSize = 42f
    }
    val descPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.parseColor("#3E3D4A")
        textSize = 36f
    }

    val textLeft = cardRect.left + 40f
    val textMaxWidth = cardRect.width() - 80f
    var currentY = cardRect.top + 820f
    currentY = drawMultilineEllipsizedText(
        canvas = canvas,
        text = content.title,
        x = textLeft,
        startY = currentY,
        maxWidth = textMaxWidth,
        maxLines = 3,
        paint = titlePaint,
        lineSpacing = 12f
    )
    currentY += 14f
    canvas.drawText(content.dateLabel, textLeft, currentY, datePaint)
    currentY += 58f
    drawMultilineEllipsizedText(
        canvas = canvas,
        text = content.description,
        x = textLeft,
        startY = currentY,
        maxWidth = textMaxWidth,
        maxLines = 7,
        paint = descPaint,
        lineSpacing = 10f
    )

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

private fun drawMultilineEllipsizedText(
    canvas: Canvas,
    text: String,
    x: Float,
    startY: Float,
    maxWidth: Float,
    maxLines: Int,
    paint: Paint,
    lineSpacing: Float
): Float {
    val words = text.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    if (words.isEmpty()) return startY

    val lines = mutableListOf<String>()
    var current = ""
    for (word in words) {
        val candidate = if (current.isEmpty()) word else "$current $word"
        if (paint.measureText(candidate) <= maxWidth) {
            current = candidate
        } else {
            if (current.isNotEmpty()) lines += current
            current = word
        }
    }
    if (current.isNotEmpty()) lines += current

    val visibleLines = lines.take(maxLines).toMutableList()
    if (lines.size > maxLines && visibleLines.isNotEmpty()) {
        var last = visibleLines.last()
        while (last.isNotEmpty() && paint.measureText("$last...") > maxWidth) {
            last = last.dropLast(1)
        }
        visibleLines[visibleLines.lastIndex] = "${last.trimEnd()}..."
    }

    val lineHeight = paint.textSize + lineSpacing
    var y = startY
    visibleLines.forEach { line ->
        canvas.drawText(line, x, y, paint)
        y += lineHeight
    }
    return y
}
