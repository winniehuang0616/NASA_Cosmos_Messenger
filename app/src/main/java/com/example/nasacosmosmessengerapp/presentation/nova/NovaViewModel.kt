package com.example.nasacosmosmessengerapp.presentation.nova

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasacosmosmessengerapp.core.util.ApodDateParseResult
import com.example.nasacosmosmessengerapp.core.util.formatPickedDateUtcMillis
import com.example.nasacosmosmessengerapp.core.util.parseApodDateInput
import com.example.nasacosmosmessengerapp.core.util.utcTodayCanonicalDate
import com.example.nasacosmosmessengerapp.data.local.RoomModule
import com.example.nasacosmosmessengerapp.data.local.entity.FavoriteApodEntity
import com.example.nasacosmosmessengerapp.data.local.entity.ChatMessageEntity
import com.example.nasacosmosmessengerapp.data.remote.NasaApiConfig
import com.example.nasacosmosmessengerapp.data.remote.NasaApiModule
import com.example.nasacosmosmessengerapp.data.remote.dto.ApodResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class NovaViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = NasaApiModule.nasaApodApiService
    private val database = RoomModule.provideDatabase(application)
    private val apodDao = database.apodDao()
    private val favoriteApodDao = database.favoriteApodDao()
    private val chatMessageDao = database.chatMessageDao()

    private val _uiState = MutableStateFlow(NovaUiState.initial())
    val uiState: StateFlow<NovaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            chatMessageDao.observeMessagesWithApod().collect { rows ->
                _uiState.update { state ->
                    val persisted = rows.map { it.toUiMessage() }
                    val persistedIds = persisted.map { it.id }.toSet()
                    // 保留尚未進 DB 的本地訊息，避免瞬間被資料庫同步洗掉
                    val localOnly = state.messages.filter { it.id !in persistedIds }
                    val transient = localOnly.filter {
                        it.isLoading || it.isError
                    }
                    val nonTransientLocal = localOnly.filterNot {
                        it.isLoading || it.isError
                    }
                    state.copy(messages = persisted + nonTransientLocal + transient)
                }
            }
        }
    }

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
        val userId = nextMessageId("u")
        val userMessage = ChatMessageUi(
            id = userId,
            text = content,
            fromUser = true
        )

        _uiState.update { s ->
            s.copy(messages = s.messages + userMessage, draft = "")
        }
        viewModelScope.launch {
            chatMessageDao.upsert(
                ChatMessageEntity(
                    id = userId,
                    role = "USER",
                    text = content,
                    apodDate = null,
                    createdAt = System.currentTimeMillis()
                )
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
        val todayDate = utcTodayCanonicalDate()
        fetchApodWithCache(
            date = todayDate,
            loadingText = "正在查詢今日 APOD...",
            errorText = "取得今日 APOD 失敗，請稍後再試。",
            remoteCall = { apiService.getTodayApod(NasaApiConfig.apiKey) }
        )
    }

    private fun fetchApodByDate(date: String) {
        fetchApodWithCache(
            date = date,
            loadingText = "正在查詢 $date 的星空圖...",
            errorText = "取得 $date 的 APOD 失敗，請稍後再試。",
            remoteCall = { apiService.getApodByDate(date = date, apiKey = NasaApiConfig.apiKey) }
        )
    }

    private fun fetchApodWithCache(
        date: String,
        loadingText: String,
        errorText: String,
        remoteCall: suspend () -> ApodResponseDto
    ) {
        viewModelScope.launch {
            val cached = apodDao.getByDate(date)
            if (cached != null) {
                val cachedMessage = cached.toApodMessage(
                    id = nextMessageId("s"),
                    prefix = "（離線快取）"
                )
                appendSystemMessage(cachedMessage.text, cachedMessage.apodCard)
                return@launch
            }

            val loadingId = appendLoadingMessage(loadingText)
            runCatching { remoteCall() }
                .onSuccess { dto ->
                    apodDao.upsert(dto.toApodEntity())
                    val message = dto.toApodMessage(id = loadingId)
                    replaceMessage(messageId = loadingId, message = message)
                    persistSystemMessage(message)
                    removeMessage(loadingId)
                }
                .onFailure {
                    val message = ChatMessageUi(
                        id = loadingId,
                        text = errorText,
                        fromUser = false,
                        isError = true
                    )
                    replaceMessage(
                        messageId = loadingId,
                        message = message
                    )
                    persistSystemMessage(message, isError = true)
                    removeMessage(loadingId)
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

    private fun removeMessage(messageId: String) {
        _uiState.update { s ->
            s.copy(messages = s.messages.filterNot { it.id == messageId })
        }
    }

    private fun appendSystemMessage(
        text: String,
        apodCard: ApodCardUi? = null
    ) {
        val id = nextMessageId("s")
        _uiState.update { s ->
            s.copy(
                messages = s.messages + ChatMessageUi(
                    id = id,
                    text = text,
                    fromUser = false,
                    apodCard = apodCard
                )
            )
        }
        viewModelScope.launch {
            chatMessageDao.upsert(
                ChatMessageEntity(
                    id = id,
                    role = "SYSTEM",
                    text = text,
                    apodDate = apodCard?.date,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
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

    private fun persistSystemMessage(
        message: ChatMessageUi,
        isError: Boolean = false
    ) {
        viewModelScope.launch {
            chatMessageDao.upsert(
                ChatMessageEntity(
                    id = message.id,
                    role = if (isError) "ERROR" else "SYSTEM",
                    text = message.text,
                    apodDate = message.apodCard?.date,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }

    private fun nextMessageId(prefix: String): String {
        return "${prefix}_${UUID.randomUUID()}"
    }

}
