package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Person(
    @SerialName("id")
    var id: Int,

    @SerialName("name")
    var name: String? = null,

    @SerialName("profile_path")
    var profilePath: String? = null,

    @SerialName("known_for_department")
    var knownForDepartment: String? = null,

    @SerialName("popularity")
    var popularity: Double? = null,

    @SerialName("known_for")
    var knownFor: List<KnownForItem> = emptyList()
)

@Serializable
data class KnownForItem(
    @SerialName("id")
    var id: Int? = null,

    @SerialName("title")
    var title: String? = null,

    @SerialName("name")
    var name: String? = null,

    @SerialName("media_type")
    var mediaType: String? = null
) {
    val displayTitle: String? get() = title ?: name
}

@Serializable
data class PeopleResponse(
    @SerialName("page")
    var page: Int? = null,

    @SerialName("results")
    var results: List<Person> = emptyList(),

    @SerialName("total_pages")
    var totalPages: Int? = null,

    @SerialName("total_results")
    var totalResults: Int? = null
)
