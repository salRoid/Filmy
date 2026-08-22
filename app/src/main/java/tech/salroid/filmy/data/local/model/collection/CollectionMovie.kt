package tech.salroid.filmy.data.local.model.collection

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollectionMovie(
    @SerialName("id")
    var id: Int,

    @SerialName("name")
    var title: String? = null,

    @SerialName("poster_path")
    var posterPath: String? = null,

    @SerialName("release_date")
    var releaseDate: String? = null,
)
