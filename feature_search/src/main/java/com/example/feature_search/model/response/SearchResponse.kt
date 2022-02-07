package com.example.feature_search.model.response


import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("Search")
    val search: List<MediaItem>,
    val totalResults: String,
    @SerializedName("Response")
    val response: String
)