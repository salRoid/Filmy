package tech.salroid.filmy.data.model

import androidx.compose.runtime.Immutable

@Immutable
data class TvShowPreview(
    val id: Int,
    val title: String,
    val posterUrl: String,
    val firstAirReadableDate: String
)