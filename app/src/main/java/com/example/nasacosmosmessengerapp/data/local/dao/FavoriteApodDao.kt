package com.example.nasacosmosmessengerapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nasacosmosmessengerapp.data.local.entity.FavoriteApodEntity
import kotlinx.coroutines.flow.Flow

data class FavoriteApodWithDetails(
    val date: String,
    val savedAt: Long,
    val title: String,
    val explanation: String,
    val mediaType: String,
    val url: String,
    val hdUrl: String?,
    val thumbnailUrl: String?
)

@Dao
interface FavoriteApodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFavorite(item: FavoriteApodEntity)

    @Query("DELETE FROM favorite_apods WHERE date = :date")
    suspend fun deleteFavorite(date: String)

    @Query("SELECT * FROM favorite_apods ORDER BY savedAt DESC")
    fun observeFavorites(): Flow<List<FavoriteApodEntity>>

    @Query(
        """
        SELECT
            f.date AS date,
            f.savedAt AS savedAt,
            a.title AS title,
            a.explanation AS explanation,
            a.mediaType AS mediaType,
            a.url AS url,
            a.hdUrl AS hdUrl,
            a.thumbnailUrl AS thumbnailUrl
        FROM favorite_apods f
        INNER JOIN apod_items a ON a.date = f.date
        ORDER BY f.savedAt DESC
        """
    )
    fun observeFavoriteDetails(): Flow<List<FavoriteApodWithDetails>>
}
