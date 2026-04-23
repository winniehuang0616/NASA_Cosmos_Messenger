package com.example.nasacosmosmessengerapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nasacosmosmessengerapp.data.local.entity.FavoriteApodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteApodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFavorite(item: FavoriteApodEntity)

    @Query("DELETE FROM favorite_apods WHERE date = :date")
    suspend fun deleteFavorite(date: String)

    @Query("SELECT * FROM favorite_apods ORDER BY savedAt DESC")
    fun observeFavorites(): Flow<List<FavoriteApodEntity>>
}
