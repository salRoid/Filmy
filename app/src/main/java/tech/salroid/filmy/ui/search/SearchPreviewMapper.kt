package tech.salroid.filmy.ui.search

import tech.salroid.filmy.data.local.model.SearchResult
import tech.salroid.filmy.data.model.SearchPreview
import tech.salroid.filmy.utility.ImageConfig
import tech.salroid.filmy.utility.formateReleaseDate
import javax.inject.Inject

class SearchPreviewMapper @Inject constructor() {

    // Make it more testable by taking from constructor
    private val posterBaseUrl = ImageConfig.BASE_URL

    fun map(searchResult: SearchResult): SearchPreview =
        SearchPreview(
            id = searchResult.id,
            title = searchResult.title.orEmpty(),
            posterUrl = posterBaseUrl + searchResult.posterPath.orEmpty(),
            readableReleaseDate = searchResult.releaseDate
                ?.let(::formateReleaseDate)
                .orEmpty()
        )
}