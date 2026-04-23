package com.example.nasacosmosmessengerapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_apods",
    foreignKeys = [
        ForeignKey(
            entity = ApodEntity::class,
            parentColumns = ["date"],
            childColumns = ["date"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("date")]
)
data class FavoriteApodEntity(
    @PrimaryKey
    val date: String,
    val savedAt: Long
)
