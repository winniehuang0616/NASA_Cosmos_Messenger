package com.example.nasacosmosmessengerapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = ApodEntity::class,
            parentColumns = ["date"],
            childColumns = ["apodDate"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("createdAt"), Index("apodDate")]
)
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val role: String,
    val text: String,
    val apodDate: String?,
    val createdAt: Long
)
