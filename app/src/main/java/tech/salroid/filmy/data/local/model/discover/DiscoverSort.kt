package tech.salroid.filmy.data.local.model.discover

enum class DiscoverSort(val apiValue: String) {
    POPULARITY_DESC("popularity.desc"),
    VOTE_AVERAGE_DESC("vote_average.desc"),
    RELEASE_DATE_DESC("primary_release_date.desc"),
    RELEASE_DATE_ASC("primary_release_date.asc")
}
