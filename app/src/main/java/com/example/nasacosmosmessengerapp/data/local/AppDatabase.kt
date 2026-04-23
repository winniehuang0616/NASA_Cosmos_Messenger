package com.example.nasacosmosmessengerapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nasacosmosmessengerapp.data.local.dao.ApodDao
import com.example.nasacosmosmessengerapp.data.local.dao.ChatMessageDao
import com.example.nasacosmosmessengerapp.data.local.dao.FavoriteApodDao
import com.example.nasacosmosmessengerapp.data.local.entity.ApodEntity
import com.example.nasacosmosmessengerapp.data.local.entity.ChatMessageEntity
import com.example.nasacosmosmessengerapp.data.local.entity.FavoriteApodEntity

@Database(
    entities = [
        ApodEntity::class,
        FavoriteApodEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun apodDao(): ApodDao
    abstract fun favoriteApodDao(): FavoriteApodDao
    abstract fun chatMessageDao(): ChatMessageDao
}
