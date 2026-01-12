package tech.salroid.filmy.data.model

import androidx.compose.runtime.Immutable

@Immutable
data class SearchPreview(
    val id: Int,
    val title: String,
    val posterUrl: String,
    val readableReleaseDate: String
)