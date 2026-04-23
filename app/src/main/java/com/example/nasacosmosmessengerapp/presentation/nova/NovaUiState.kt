package com.example.nasacosmosmessengerapp.presentation.nova

data class ChatMessageUi(
    val id: String,
    val text: String,
    val fromUser: Boolean
)

data class NovaUiState(
    val messages: List<ChatMessageUi>,
    val draft: String,
    val showDatePicker: Boolean
) {
    companion object {
        fun initial(): NovaUiState = NovaUiState(
            messages = listOf(
                ChatMessageUi(
                    id = "m1",
                    text = "你好，我是 Nova。輸入日期（例如 1995-06-16）即可查詢當日 APOD。",
                    fromUser = false
                ),
                ChatMessageUi(
                    id = "m2",
                    text = "1995-06-16",
                    fromUser = true
                ),
                ChatMessageUi(
                    id = "m3",
                    text = "（此處為 APOD 卡片預留，之後會接上 API 與圖片）",
                    fromUser = false
                )
            ),
            draft = "",
            showDatePicker = false
        )
    }
}
