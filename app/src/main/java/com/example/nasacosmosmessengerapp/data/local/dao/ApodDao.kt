package com.example.nasacosmosmessengerapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nasacosmosmessengerapp.data.local.entity.ApodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ApodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(apod: ApodEntity)

    @Query("SELECT * FROM apod_items WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): ApodEntity?

    @Query("SELECT * FROM apod_items WHERE date = :date LIMIT 1")
    fun observeByDate(date: String): Flow<ApodEntity?>
}
