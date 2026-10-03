package tech.salroid.filmy.ui.movies

import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.data.model.MoviePreview
import tech.salroid.filmy.utility.ImageConfig
import tech.salroid.filmy.utility.formatReleaseDate
import javax.inject.Inject

class MoviePreviewMapper @Inject constructor() {

    // Make it more testable by taking from constructor
    private val posterBaseUrl = ImageConfig.BASE_URL

    fun map(movie: Movie): MoviePreview =
        MoviePreview(
            id = movie.id,
            title = movie.title.orEmpty(),
            posterUrl = posterBaseUrl + movie.posterPath.orEmpty(),
            readableReleaseDate = movie.releaseDate
                ?.let(::formatReleaseDate)
                .orEmpty()
        )
}