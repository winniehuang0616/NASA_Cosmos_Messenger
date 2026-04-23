package com.example.nasacosmosmessengerapp.presentation.nova

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasacosmosmessengerapp.core.util.ApodDateParseResult
import com.example.nasacosmosmessengerapp.core.util.parseApodDateInput
import com.example.nasacosmosmessengerapp.data.local.RoomModule
import com.example.nasacosmosmessengerapp.data.local.entity.ApodEntity
import com.example.nasacosmosmessengerapp.data.local.entity.FavoriteApodEntity
import com.example.nasacosmosmessengerapp.data.remote.NasaApiConfig
import com.example.nasacosmosmessengerapp.data.remote.NasaApiModule
import com.example.nasacosmosmessengerapp.data.remote.dto.ApodResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class NovaViewModel(application: Application) : AndroidViewModel(application) {

    private var messageSeq: Long = 1000L
    private val apiService = NasaApiModule.nasaApodApiService
    private val database = RoomModule.provideDatabase(application)
    private val apodDao = database.apodDao()
    private val favoriteApodDao = database.favoriteApodDao()

    private val _uiState = MutableStateFlow(NovaUiState.initial())
    val uiState: StateFlow<NovaUiState> = _uiState.asStateFlow()

    fun onDraftChange(value: String) {
        _uiState.update { it.copy(draft = value) }
    }

    fun onOpenDatePicker() {
        _uiState.update { it.copy(showDatePicker = true) }
    }

    fun onDismissDatePicker() {
        _uiState.update { it.copy(showDatePicker = false) }
    }

    fun onDateSelected(millis: Long) {
        _uiState.update {
            it.copy(
                draft = formatPickedDateUtcMillis(millis),
                showDatePicker = false
            )
        }
    }

    fun onSendClick() {
        val content = _uiState.value.draft.trim()
        if (content.isBlank()) return

        _uiState.update { s ->
            s.copy(
                messages = s.messages + ChatMessageUi(
                    id = nextMessageId("u"),
                    text = content,
                    fromUser = true
                ),
                draft = ""
            )
        }

        when (val dateResult = parseApodDateInput(content)) {
            ApodDateParseResult.NotDateLike -> fetchTodayApod()
            ApodDateParseResult.InvalidDate -> {
                appendSystemMessage("無效日期，請確認日期是否存在（例如 2025-02-31 無效）。")
            }
            is ApodDateParseResult.OutOfRange -> {
                appendSystemMessage("日期超出範圍，請輸入 ${dateResult.minDate} 到 ${dateResult.maxDate}。")
            }
            is ApodDateParseResult.Valid -> fetchApodByDate(dateResult.canonicalDate)
        }
    }

    private fun fetchTodayApod() {
        val loadingId = appendLoadingMessage("正在查詢今日 APOD...")
        viewModelScope.launch {
            runCatching {
                apiService.getTodayApod(NasaApiConfig.apiKey)
            }.onSuccess { dto ->
                apodDao.upsert(dto.toApodEntity())
                replaceMessage(
                    messageId = loadingId,
                    message = dto.toApodMessage(id = loadingId)
                )
            }.onFailure {
                replaceMessage(
                    messageId = loadingId,
                    message = ChatMessageUi(
                        id = loadingId,
                        text = "取得今日 APOD 失敗，請稍後再試。",
                        fromUser = false,
                        isError = true
                    )
                )
            }
        }
    }

    private fun fetchApodByDate(date: String) {
        val loadingId = appendLoadingMessage("正在查詢 $date 的星空圖...")
        viewModelScope.launch {
            runCatching {
                apiService.getApodByDate(date = date, apiKey = NasaApiConfig.apiKey)
            }.onSuccess { dto ->
                apodDao.upsert(dto.toApodEntity())
                replaceMessage(
                    messageId = loadingId,
                    message = dto.toApodMessage(id = loadingId)
                )
            }.onFailure {
                replaceMessage(
                    messageId = loadingId,
                    message = ChatMessageUi(
                        id = loadingId,
                        text = "取得 $date 的 APOD 失敗，請稍後再試。",
                        fromUser = false,
                        isError = true
                    )
                )
            }
        }
    }

    private fun appendLoadingMessage(text: String): String {
        val loadingId = nextMessageId("loading")
        _uiState.update { s ->
            s.copy(
                messages = s.messages + ChatMessageUi(
                    id = loadingId,
                    text = text,
                    fromUser = false,
                    isLoading = true
                )
            )
        }
        return loadingId
    }

    private fun replaceMessage(messageId: String, message: ChatMessageUi) {
        _uiState.update { s ->
            s.copy(
                messages = s.messages.map { old ->
                    if (old.id == messageId) message else old
                }
            )
        }
    }

    private fun appendSystemMessage(text: String) {
        _uiState.update { s ->
            s.copy(
                messages = s.messages + ChatMessageUi(
                    id = nextMessageId("s"),
                    text = text,
                    fromUser = false
                )
            )
        }
    }

    private fun ApodResponseDto.toApodMessage(id: String): ChatMessageUi {
        val image = if (mediaType == "image") {
            hdUrl ?: url
        } else {
            thumbnailUrl ?: url
        }
        return ChatMessageUi(
            id = id,
            text = " ${date} 的星空圖長這樣：",
            fromUser = false,
            apodCard = ApodCardUi(
                date = date,
                title = title,
                description = explanation,
                imageUrl = image
            )
        )
    }

    private fun ApodResponseDto.toApodEntity(): ApodEntity {
        return ApodEntity(
            date = date,
            title = title,
            explanation = explanation,
            mediaType = mediaType,
            url = url,
            hdUrl = hdUrl,
            thumbnailUrl = thumbnailUrl,
            copyright = copyright,
            updatedAt = System.currentTimeMillis()
        )
    }

    fun onApodCardLongPress(card: ApodCardUi) {
        viewModelScope.launch {
            runCatching {
                favoriteApodDao.upsertFavorite(
                    FavoriteApodEntity(
                        date = card.date,
                        savedAt = System.currentTimeMillis()
                    )
                )
            }.onSuccess {
                appendSystemMessage("已加入收藏：${card.title}")
            }.onFailure {
                appendSystemMessage("加入收藏失敗，請稍後再試。")
            }
        }
    }

    private fun nextMessageId(prefix: String): String {
        messageSeq += 1
        return "${prefix}_${messageSeq}"
    }

    private fun formatPickedDateUtcMillis(millis: Long): String {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = millis }
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH) + 1
        val d = cal.get(Calendar.DAY_OF_MONTH)
        return String.format(Locale.getDefault(), "%d/%02d/%02d", y, m, d)
    }
}
