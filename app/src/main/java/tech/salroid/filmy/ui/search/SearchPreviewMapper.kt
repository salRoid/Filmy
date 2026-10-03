package tech.salroid.filmy.ui.search

import tech.salroid.filmy.data.local.model.SearchResult
import tech.salroid.filmy.data.model.SearchPreview
import tech.salroid.filmy.utility.ImageConfig
import tech.salroid.filmy.utility.formatReleaseDate
import javax.inject.Inject

class SearchPreviewMapper @Inject constructor() {

    // Make it more testable by taking from constructor
    private val posterBaseUrl = ImageConfig.BASE_URL

    fun map(searchResult: SearchResult): SearchPreview =
        SearchPreview(
            id = searchResult.id,
            title = (searchResult.title ?: searchResult.name).orEmpty(),
            posterUrl = posterBaseUrl + (searchResult.posterPath ?: searchResult.profilePath).orEmpty(),
            readableReleaseDate = (searchResult.releaseDate ?: searchResult.firstAirDate)
                ?.let(::formatReleaseDate)
                .orEmpty(),
            mediaType = searchResult.mediaType
        )
}