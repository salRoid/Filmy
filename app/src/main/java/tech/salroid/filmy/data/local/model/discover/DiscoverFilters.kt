package tech.salroid.filmy.data.local.model.discover

data class DiscoverFilters(
    val genreIds: Set<Int> = emptySet(),
    val year: Int? = null,
    val minRating: Float? = null,
    val sortBy: DiscoverSort = DiscoverSort.POPULARITY_DESC,
    val keywordId: Int? = null,
    val keywordName: String? = null
)
