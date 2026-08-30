package tech.salroid.filmy.ui.shows

import tech.salroid.filmy.data.local.model.TvShow
import tech.salroid.filmy.data.model.TvShowPreview
import tech.salroid.filmy.utility.ImageConfig
import tech.salroid.filmy.utility.formateReleaseDate
import javax.inject.Inject

class TvShowsPreviewMapper @Inject constructor() {
    private val posterBaseUrl = ImageConfig.BASE_URL

    fun map(tvShow: TvShow): TvShowPreview =
        TvShowPreview(
            id = tvShow.id,
            title = tvShow.name.orEmpty(),
            posterUrl = posterBaseUrl + tvShow.posterPath.orEmpty(),
            firstAirReadableDate = tvShow.firstAirDate
                ?.let(::formateReleaseDate)
                .orEmpty()
        )
}