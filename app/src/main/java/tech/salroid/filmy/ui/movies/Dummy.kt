package tech.salroid.filmy.ui.movies

import tech.salroid.filmy.data.model.MoviePreview
import tech.salroid.filmy.data.model.TvShowPreview
import kotlin.math.floor

val dummyMoviePreview = MoviePreview(
    id = (floor(Math.random() * 9000) + 1000).toInt(),
    title = "People We Meet On Vacation",
    posterUrl = "",
    readableReleaseDate = "06 Jan 2026"
)

val dummyShowPreview = TvShowPreview(
    id = (floor(Math.random() * 9000) + 1000).toInt(),
    title = "Hunger Games",
    posterUrl = "",
    firstAirReadableDate = "06 Jan 2026"
)