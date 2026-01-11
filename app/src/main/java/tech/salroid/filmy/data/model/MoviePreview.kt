package tech.salroid.filmy.data.model

import androidx.compose.runtime.Immutable

@Immutable
data class MoviePreview(
    val id: Int,
    val title: String,
    val posterUrl: String,
    val readableReleaseDate: String
)