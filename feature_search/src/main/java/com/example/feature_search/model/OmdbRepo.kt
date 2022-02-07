package com.example.feature_search.model

import android.app.Application
import com.example.feature_search.model.local.OmdbDatabase
import com.example.feature_search.model.remote.OmdbService
import com.example.feature_search.model.response.MediaItem
import com.example.feature_search.model.response.SearchResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.nio.file.Files.delete

/**
 * Using object makes this instance a Singleton Class, which means only 1 instance of this class will ever exist
 */
class OmdbRepo(app: Application) {

    private val mediaItemDao by lazy { OmdbDatabase.getInstance(app).mediaItemDao() }

    val mediaItems: Flow<List<MediaItem>> = mediaItemDao.getAll()

    /**
     * suspend -> makes this a coroutine function so we can move its operations to another thread
     * withContext -> part of coroutines we use this to change the Thread
     * Dispatchers -> Use this to select the thread
     *
     * This method uses OMDB Service instance to fetch for media item relating to the query passed in
     */
    suspend fun searchByQuery(
        query: String
    ): Result<SearchResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = OmdbService.instance.searchByQuery(query)
            val searchResponse = response.body()!!
            mediaItemDao.deleteAll()
            mediaItemDao.insertAll(*searchResponse.search.toTypedArray())
            Result.success(searchResponse) // return success if there's a body(SearchResponse)
        } catch (ex: Exception) {
            Result.failure(ex) // return failure, means call failed or body was null
        }
    }
}