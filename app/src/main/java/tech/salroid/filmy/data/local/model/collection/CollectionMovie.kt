package tech.salroid.filmy.data.local.model.collection

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class CollectionMovie(
    @SerialName("id")
    var id: Int,

    // TMDB sends a collection's parts as movies, whose title is under
    // "title" ("name" is the collection's own field, kept as a fallback).
    @SerialName("title")
    @JsonNames("name")
    var title: String? = null,

    @SerialName("poster_path")
    var posterPath: String? = null,

    @SerialName("release_date")
    var releaseDate: String? = null,
)
