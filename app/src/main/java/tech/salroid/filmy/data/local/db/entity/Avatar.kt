package tech.salroid.filmy.data.local.db.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Avatar(
    @SerialName("gravatar")
    var gravatar: Gravatar? = null,

    @SerialName("tmdb")
    var tmdb: Tmdb? = null
)