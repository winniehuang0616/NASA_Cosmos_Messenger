package com.example.nasacosmosmessengerapp.data.local

import android.content.Context
import androidx.room.Room

object RoomModule {
    private const val DATABASE_NAME = "nasa_cosmos_messenger.db"

    @Volatile
    private var instance: AppDatabase? = null

    fun provideDatabase(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build().also { db ->
                instance = db
            }
        }
    }
}
