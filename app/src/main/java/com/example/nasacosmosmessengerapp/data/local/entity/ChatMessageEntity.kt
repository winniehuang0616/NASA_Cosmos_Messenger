package com.example.nasacosmosmessengerapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chat_messages",
    indices = [Index("createdAt")]
)
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val role: String,
    val text: String,
    val apodDate: String?,
    val createdAt: Long
)
