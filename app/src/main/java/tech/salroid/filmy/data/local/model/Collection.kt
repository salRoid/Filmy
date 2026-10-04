package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Collection(

    @SerialName("id")
    var id: Int? = null,

    @SerialName("name")
    var name: String? = null,

    @SerialName("poster_path")
    var posterPath: String? = null,

    @SerialName("backdrop_path")
    var backdropPath: String? = null
)
