package tech.salroid.filmy.data.local.model.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbList(
    @SerialName("id")
    var id: Int,

    @SerialName("name")
    var name: String? = null,

    @SerialName("description")
    var description: String? = null,

    @SerialName("number_of_items")
    var itemCount: Int? = null,

    @SerialName("poster_path")
    var posterPath: String? = null,
)
