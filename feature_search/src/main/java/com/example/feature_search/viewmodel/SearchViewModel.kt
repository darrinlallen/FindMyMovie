package com.example.feature_search.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.feature_search.model.OmdbRepo
import com.example.feature_search.model.response.MediaItem
import com.example.feature_search.model.response.SearchResponse
import com.example.feature_search.util.ViewState
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SearchViewModel(app: Application) : AndroidViewModel(app) {

    private val omdbRepo by lazy { OmdbRepo(getApplication()) }

    private val _viewState = MutableLiveData<ViewState>(ViewState.NoQuery)
    val viewState: LiveData<ViewState> get() = _viewState

    init {
        viewModelScope.launch {
            omdbRepo.mediaItems.firstOrNull()?.let { mediaItems ->
                _viewState.value = ViewState.Success(mediaItems)
            }
        }
    }

    /**
     * Holds query being searched
     */
    var query: String = ""
        set(value) {
            when (value.length) { // run different logic based on how many character in the query
                0 -> _viewState.value = ViewState.NoQuery
                in 1 until 3 -> _viewState.value = ViewState.Failure.InvalidQuery
                else -> if (query != value) searchForMediaItems(value)
            }
            // field(aka query) needs this to actually set the new value,
            // without this the variable wont be updated
            field = value
        }

    private fun searchForMediaItems(query: String) {
        // Update ViewState to loading so view can display a loader
        _viewState.value = ViewState.Loading

        viewModelScope.launch {
            // Using the repo instance to fetch user query
            val result: Result<SearchResponse> = omdbRepo.searchByQuery(query)
            // If it's NOT NULL we have an error else if it's null we assume 2 things
            //      1) there is no error
            //      2) there was an unknown error
            val exception: String? = result.exceptionOrNull()?.message
            // We either get a list or null
            val mediaItems: List<MediaItem>? = result.getOrNull()?.search

            // Calculate what the ViewState will be based on variables above
            val viewState = when {
                // Error Check: True if call was successful but no results were found
                mediaItems?.isEmpty() == true -> ViewState.Failure.NoMediaItemsFound
                // Error Check: True if call was NOT successful
                mediaItems.isNullOrEmpty() -> {
                    if (exception != null) ViewState.Failure.WhileFetching(exception)
                    else ViewState.Failure.SomethingWentWrong
                }
                // Passed all error checks we definitely have a list of media items
                else -> ViewState.Success(mediaItems)
            }

            // Update the ViewState
            _viewState.value = viewState
        }
    }

}