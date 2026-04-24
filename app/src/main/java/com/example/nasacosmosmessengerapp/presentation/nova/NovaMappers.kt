package com.example.nasacosmosmessengerapp.presentation.nova

import com.example.nasacosmosmessengerapp.data.local.dao.ChatMessageWithApod
import com.example.nasacosmosmessengerapp.data.local.entity.ApodEntity
import com.example.nasacosmosmessengerapp.data.remote.dto.ApodResponseDto

fun ApodResponseDto.toApodMessage(id: String): ChatMessageUi {
    return ChatMessageUi(
        id = id,
        text = " ${date} 的星空圖長這樣：",
        fromUser = false,
        apodCard = ApodCardUi(
            date = date,
            title = title,
            description = explanation,
            imageUrl = resolveApodImageUrl(
                mediaType = mediaType,
                url = url,
                hdUrl = hdUrl,
                thumbnailUrl = thumbnailUrl
            ),
            mediaType = mediaType,
            contentUrl = url
        )
    )
}

fun ApodEntity.toApodMessage(id: String, prefix: String = ""): ChatMessageUi {
    return ChatMessageUi(
        id = id,
        text = "$prefix ${date} 的星空圖長這樣：".trim(),
        fromUser = false,
        apodCard = ApodCardUi(
            date = date,
            title = title,
            description = explanation,
            imageUrl = resolveApodImageUrl(
                mediaType = mediaType,
                url = url,
                hdUrl = hdUrl,
                thumbnailUrl = thumbnailUrl
            ),
            mediaType = mediaType,
            contentUrl = url
        )
    )
}

fun ApodResponseDto.toApodEntity(nowMillis: Long = System.currentTimeMillis()): ApodEntity {
    return ApodEntity(
        date = date,
        title = title,
        explanation = explanation,
        mediaType = mediaType,
        url = url,
        hdUrl = hdUrl,
        thumbnailUrl = thumbnailUrl,
        copyright = copyright,
        updatedAt = nowMillis
    )
}

fun ChatMessageWithApod.toUiMessage(): ChatMessageUi {
    val card = if (apodDate != null && title != null && explanation != null && url != null) {
        ApodCardUi(
            date = apodDate,
            title = title,
            description = explanation,
            imageUrl = resolveApodImageUrl(
                mediaType = mediaType,
                url = url,
                hdUrl = hdUrl,
                thumbnailUrl = thumbnailUrl
            ),
            mediaType = mediaType ?: "image",
            contentUrl = url
        )
    } else {
        null
    }
    return ChatMessageUi(
        id = id,
        text = text,
        fromUser = role == "USER",
        apodCard = card,
        isError = role == "ERROR"
    )
}

private fun resolveApodImageUrl(
    mediaType: String?,
    url: String,
    hdUrl: String?,
    thumbnailUrl: String?
): String {
    return if (mediaType == "image") {
        hdUrl ?: url
    } else {
        thumbnailUrl ?: ""
    }
}
