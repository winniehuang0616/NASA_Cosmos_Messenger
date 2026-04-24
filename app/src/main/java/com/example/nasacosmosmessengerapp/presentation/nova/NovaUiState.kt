package com.example.nasacosmosmessengerapp.presentation.nova

data class ChatMessageUi(
    val id: String,
    val text: String,
    val fromUser: Boolean,
    val apodCard: ApodCardUi? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false
)

data class ApodCardUi(
    val date: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val mediaType: String,
    val contentUrl: String
)

data class NovaUiState(
    val messages: List<ChatMessageUi>,
    val draft: String,
    val showDatePicker: Boolean
) {
    companion object {
        fun initial(): NovaUiState = NovaUiState(
            messages = emptyList(),
            draft = "",
            showDatePicker = false
        )
    }
}
