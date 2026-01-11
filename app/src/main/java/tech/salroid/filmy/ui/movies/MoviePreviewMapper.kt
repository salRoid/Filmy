package tech.salroid.filmy.ui.movies

import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.data.model.MoviePreview
import tech.salroid.filmy.utility.ImageConfig
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class MoviePreviewMapper @Inject constructor() {

    // Make it more testable by taking from constructor
    private val posterBaseUrl = ImageConfig.BASE_URL

    fun map(movie: Movie): MoviePreview =
        MoviePreview(
            id = movie.id,
            title = movie.title.orEmpty(),
            posterUrl = movie.posterPath?.let { posterBaseUrl + it }.orEmpty(),
            readableReleaseDate = movie.releaseDate?.let(::formateReleaseDate).orEmpty()
        )

    private fun formateReleaseDate(raw: String): String {
        return runCatching {
            LocalDate.parse(raw)
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        }.getOrElse { "" }
    }
}