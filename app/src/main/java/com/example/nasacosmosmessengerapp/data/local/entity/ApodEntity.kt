package com.example.nasacosmosmessengerapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apod_items")
data class ApodEntity(
    @PrimaryKey
    val date: String,
    val title: String,
    val explanation: String,
    val mediaType: String,
    val url: String,
    val hdUrl: String?,
    val thumbnailUrl: String?,
    val copyright: String?,
    val updatedAt: Long
)
