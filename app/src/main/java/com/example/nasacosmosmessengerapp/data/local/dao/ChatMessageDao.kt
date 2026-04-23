package com.example.nasacosmosmessengerapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nasacosmosmessengerapp.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

data class ChatMessageWithApod(
    val id: String,
    val role: String,
    val text: String,
    val apodDate: String?,
    val createdAt: Long,
    val title: String?,
    val explanation: String?,
    val mediaType: String?,
    val url: String?,
    val hdUrl: String?,
    val thumbnailUrl: String?
)

@Dao
interface ChatMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(messages: List<ChatMessageEntity>)

    @Query("SELECT * FROM chat_messages ORDER BY createdAt ASC")
    fun observeAll(): Flow<List<ChatMessageEntity>>

    @Query(
        """
        SELECT
            c.id AS id,
            c.role AS role,
            c.text AS text,
            c.apodDate AS apodDate,
            c.createdAt AS createdAt,
            a.title AS title,
            a.explanation AS explanation,
            a.mediaType AS mediaType,
            a.url AS url,
            a.hdUrl AS hdUrl,
            a.thumbnailUrl AS thumbnailUrl
        FROM chat_messages c
        LEFT JOIN apod_items a ON a.date = c.apodDate
        ORDER BY c.createdAt ASC
        """
    )
    fun observeMessagesWithApod(): Flow<List<ChatMessageWithApod>>
}
