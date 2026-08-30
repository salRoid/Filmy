package tech.salroid.filmy.data.local.model

import com.google.gson.annotations.SerializedName

data class Person(
    @SerializedName("id")
    var id: Int,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("profile_path")
    var profilePath: String? = null,

    @SerializedName("known_for_department")
    var knownForDepartment: String? = null,

    @SerializedName("popularity")
    var popularity: Double? = null,

    @SerializedName("known_for")
    var knownFor: List<KnownForItem> = emptyList()
)

data class KnownForItem(
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName(value = "title", alternate = ["name"])
    var title: String? = null,

    @SerializedName("media_type")
    var mediaType: String? = null
)

data class PeopleResponse(
    @SerializedName("page")
    var page: Int? = null,

    @SerializedName("results")
    var results: List<Person> = emptyList(),

    @SerializedName("total_pages")
    var totalPages: Int? = null,

    @SerializedName("total_results")
    var totalResults: Int? = null
)
