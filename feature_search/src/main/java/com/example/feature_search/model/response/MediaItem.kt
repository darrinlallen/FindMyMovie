package com.example.feature_search.model.response


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "media_item")
data class MediaItem(
    @SerializedName("Title")
    val title: String,
    @SerializedName("Year")
    val year: String,
    @PrimaryKey
    @ColumnInfo(name = "imdb_id")
    val imdbID: String,
    @SerializedName("Type")
    val type: String,
    @SerializedName("Poster")
    val poster: String
)