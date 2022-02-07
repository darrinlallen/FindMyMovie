package com.example.feature_search.model.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.feature_search.model.response.MediaItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaItemDao {

   @Query("SELECT * FROM media_item")
    fun getAll(): Flow<List<MediaItem>>

    @Insert
    suspend fun insertAll(vararg users: MediaItem)

    @Query("DELETE FROM media_item")
    fun deleteAll()
}